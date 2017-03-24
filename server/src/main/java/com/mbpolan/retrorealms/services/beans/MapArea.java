package com.mbpolan.retrorealms.services.beans;

import com.mbpolan.retrorealms.beans.responses.AbstractResponse;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    private List<List<Tile>> tiles;

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
     * @param tiles The rectangle of tiles in this area, in row-major order.
     * @param width The width of the map area, in tiles.
     * @param height The height of the map area, in tiles.
     * @param tileSize The size (width and height) of a single, square tile.
     */
    public MapArea(List<List<Tile>> tiles, int width, int height, int tileSize) {
        this.players = new ArrayList<>();
        this.tiles = tiles;
        this.width = width;
        this.height = height;
        this.tileSize = tileSize;
        this.state = new GameState();
        this.planes = new ArrayList<>();

        // compute our initial collision planes using the tiles that have bounding boxes
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Tile tile = tiles.get(y).get(x);

                if (tile.hasBoundingBox()) {
                    // the tile's collision plane will be its position on the map
                    Rectangle plane = tile.getBoundingBox().copy();
                    plane.translate(x * tileSize, y * tileSize);

                    this.planes.add(plane);
                }
            }
        }
    }

    /**
     * Adds a player to this map area.
     *
     * @param player The player to add.
     */
    public void addPlayer(Player player) {
        this.players.add(player);
        this.state.addChangedPlayer(player);
    }

    /**
     * Remove a player from this map area.
     *
     * @param player The player to remove.
     */
    public void removePlayer(Player player) {
        this.players.remove(player);
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
     * @return A rectangle of tiles, in row-major order.
     */
    public List<Integer> getTileIds() {
        List<Integer> ids = new ArrayList<>();
        this.tiles.forEach(row -> row.forEach(col -> ids.add(col.getId())));

        return ids;
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
        return computePlayerMovement(player, direction, false);
    }

    /**
     * Computes a player movement and updates their position if needed.
     *
     * @param player The moving player.
     * @return true if the player was moved, false if not.
     */
    public boolean movePlayer(Player player) {
        return computePlayerMovement(player, player.getDirection(), true);
    }

    /**
     * Attempts to move a player in their current direction, and optionally commits the change.
     *
     * @param player The moving player.
     * @param direction The direction in which to move the player.
     * @param commit true to commit the movement, false to rollback if successful.
     * @return true if the movement succeeded, false if not.
     */
    private boolean computePlayerMovement(Player player, Direction direction, boolean commit) {
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

        // has the player went outside the bounds of the map area or collided with something?
        if ((rect.getX1() < 0 || rect.getX2() > width * this.tileSize) ||
                (rect.getY1() < 0 || rect.getY2() > height * this.tileSize) ||
                this.planes.stream().filter(p -> p.overlaps(rect)).findAny().isPresent()) {

            // rollback the movement
            rect.translate(-dx, -dy);
            return false;
        }

        // should we commit the movement change?
        if (!commit) {
            rect.translate(-dx, -dy);
        }

        else {
            state.addChangedPlayer(player);
        }

        return true;
    }
}
