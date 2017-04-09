package com.mbpolan.retrorealms.services.beans;

import com.mbpolan.retrorealms.beans.responses.AbstractResponse;
import com.mbpolan.retrorealms.services.map.Door;
import com.mbpolan.retrorealms.services.map.Layer;
import com.mbpolan.retrorealms.services.map.Tile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Represents a single area of the map that contains tiles, players and other entities.
 *
 * Players can be added or removed from map areas. Each map area is responsible for computing its current
 * "game state", which describes the current conditions in that area. The tiles are represented as a rectangle,
 * in row-major order.
 *
 * @author mbpolan
 */
public class MapArea extends Lockable {

    // list of players currently in this area
    private List<Player> players;

    // matrix of static tiles in this area, in row-major order
    private List<Layer> layers;

    // list of doors found in this map area
    private List<Door> doors;

    // list of static and dynamic collision planes in this area
    private List<Rectangle> planes;

    // map area dimensions and current state
    private int width;
    private int height;
    private int tileSize;
    private GameState state;

    /**
     * Creates a map area with tiles and geometry.
     *
     * The initial game state contains no players.
     *
     * @param width The width of the map area, in tiles.
     * @param height The height of the map area, in tiles.
     * @param tileSize The size (width and height) of a single, square tile.
     * @param layers The list of rectangles of tiles in this area.
     * @param doors The list of doors in this area.
     */
    public MapArea(int width, int height, int tileSize, List<Layer> layers, List<Door> doors) {
        this.players = new ArrayList<>();
        this.width = width;
        this.height = height;
        this.tileSize = tileSize;
        this.layers = layers;
        this.doors = doors;
        this.state = new GameState();
        this.planes = new ArrayList<>();

        // compute the initial collection of collision planes
        computePlanes();
    }

    /**
     * Adds a player to this map area.
     *
     * @param player The player to add.
     */
    public void addPlayer(Player player) {
        this.players.add(player);
        this.planes.add(player.plane());
        this.state.addChangedPlayer(player);
    }

    /**
     * Remove a player from this map area.
     *
     * @param player The player to remove.
     */
    public void removePlayer(Player player) {
        this.players.remove(player);
        this.planes.remove(player.plane());
    }

    /**
     * Returns a list of all players currently on this map area.
     *
     * @return The set of players.
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Sends a message to all players in this map area.
     *
     * @param message The message to send.
     */
    public void sendToAll(AbstractResponse message) {
        players.forEach(p -> p.send(message));
    }

    /**
     * Sends a message to all players in this map area excluding one.
     *
     * @param message The message to send.
     * @param excluded The player to not send the message to.
     */
    public void sendToAll(AbstractResponse message, Player excluded) {
        players.stream()
                .filter(p -> p.getId() != excluded.getId())
                .forEach(p -> p.send(message));
    }

    /**
     * Returns a geometry of this area represented by tiles.
     *
     * @return A list of rectangles of tiles in each layer, in row-major order..
     */
    public List<List<Integer>> getTileIds() {
        return this.layers.stream()
                .map(l -> l.getTiles().stream()
                        .flatMap(List::stream)
                        .map(col -> col == null ? 0 : col.getId())
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    /**
     * Returns the width of the map area, in tiles.
     *
     * @return The amount of tiles wide.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the height of the map area, in tiles.
     *
     * @return The amount of tiles high.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Returns the last computed state of the map area.
     *
     * @return The last game state.
     */
    public GameState getState() {
        return state;
    }

    /**
     * Recomputes the current state of this map area and determines if something changed.
     *
     * @return The previous game state if changed, null otherwise.
     */
    public GameState popState() {
        GameState previous = null;
        if (state.isDirty()) {
            previous = state.reset();
        }

        return previous;
    }

    /**
     * Tests if a player can move in a given direction.
     *
     * @param player The moving player.
     * @param direction The direction in which the player intends to move.
     * @return true if the move can be done, false if not.
     */
    public boolean canPlayerMove(Player player, Direction direction) {
        return computePlayerMovement(player, direction, false).isValid();
    }

    /**
     * Computes a player movement and updates their position if needed.
     *
     * @param player The moving player.
     * @return The action that resulted from the movement.
     */
    public MoveAction movePlayer(Player player) {
        return computePlayerMovement(player, player.getDirection(), true);
    }

    /**
     * Recomputes the collection of collision planes based on the layers of tiles in the area.
     *
     */
    private void computePlanes() {
        int pixelWidth = width * tileSize;
        int pixelHeight = height * tileSize;

        // create "virtual" planes that define the bounds of the map area itself - top, bottom, left, right
        this.planes.add(new Rectangle(0, -tileSize, pixelWidth, 0));
        this.planes.add(new Rectangle(0, pixelHeight + tileSize, pixelWidth, pixelHeight + tileSize));
        this.planes.add(new Rectangle(-tileSize, 0, 0, pixelHeight));
        this.planes.add(new Rectangle(pixelWidth, 0, pixelWidth + tileSize, pixelHeight));

        this.layers.forEach(layer -> {
            // compute collision planes using the tiles that have bounding boxes
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    Tile tile = layer.getTiles().get(y).get(x);

                    if (tile != null && tile.hasBoundingBoxes()) {
                        // transform each bounding box into a collision plane
                        for (Rectangle bbox : tile.getBoundingBoxes()) {
                            Rectangle plane = bbox.copy();

                            // the plane's position relative to the tile's position on the map)
                            plane.translate((x * tileSize) + plane.getX1(), (y * tileSize) + plane.getY1());
                            this.planes.add(plane);
                        }
                    }
                }
            }
        });
    }

    /**
     * Attempts to move a player in their current direction, and optionally commits the change.
     *
     * @param player The moving player.
     * @param direction The direction in which to move the player.
     * @param commit true to commit the movement, false to rollback if successful.
     * @return The action that resulted from the movement.
     */
    private MoveAction computePlayerMovement(Player player, Direction direction, boolean commit) {
        // compute a delta movement vector
        int dx = 0, dy = 0;
        switch (direction) {
            case UP:
                dy = -player.getSpeed();
                break;
            case DOWN:
                dy = player.getSpeed();
                break;
            case LEFT:
                dx = -player.getSpeed();
                break;
            case RIGHT:
                dx = player.getSpeed();
                break;
        }

        // move the player to their new position
        Rectangle rect = player.plane();
        rect.translate(dx, dy);

        // has the player went outside the bounds of the map area?
        if (findCollision(rect).isPresent()) {
            // rollback the movement
            rect.translate(-dx, -dy);
            return MoveAction.collision();
        }

        // should we commit the movement change?
        if (!commit) {
            rect.translate(-dx, -dy);
        }

        else {
            state.addChangedPlayer(player);

            // has the player reached a door?
            Door door = findDoorCollision(rect);
            if (door != null) {
                return MoveAction.moveToDoor(door);
            }
        }

        return MoveAction.move();
    }

    /**
     * Detects is a given rectangle overlaps with any other collision plane in the area.
     *
     * @param rect The rectangle to test.
     * @return true if there is a collision, false if not.
     */
    private Optional<Rectangle> findCollision(Rectangle rect) {
        return this.planes.stream()
                .filter(p -> p != rect && p.overlaps(rect))
                .findAny();
    }

    /**
     * Detects if a given rectangle overlaps with any door plane in the area.
     *
     * @param rect The rectangle to test.
     * @return The first {@link Door} that the rectangle collides with, or null if none.
     */
    private Door findDoorCollision(Rectangle rect) {
        return this.doors.stream()
                .filter(d -> d.getBounds().overlaps(rect))
                .findFirst()
                .orElse(null);
    }
}
