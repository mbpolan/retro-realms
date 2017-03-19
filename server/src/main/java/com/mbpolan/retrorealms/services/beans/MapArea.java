package com.mbpolan.retrorealms.services.beans;

import com.mbpolan.retrorealms.beans.responses.AbstractResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single area of the map that contains tiles, players and other entities.
 *
 * Players can be added or removed from map areas. Each map area is responsible for computing its current
 * "game state", which describes the current conditions in that area. The tiles are represented as a rectangle,
 * in row-major order.
 *
 * @author Mike Polan
 */
public class MapArea {

    private List<Player> players;
    private List<List<Tile>> tiles;
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
    }

    /**
     * Adds a player to this map area.
     *
     * @param player The player to add.
     */
    public void addPlayer(Player player) {
        this.players.add(player);
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
     * @return The list of players.
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
     * Computes a player movement and updates their position if needed.
     *
     * @param player The moving player.
     * @param multiplier The speed multiplier.
     */
    public boolean movePlayer(Player player, int multiplier) {
        int delay = player.getSpeed() * multiplier;
        if (System.currentTimeMillis() - player.getLastMovement() < delay) {
            return false;
        }

        // compute a delta movement vector
        int dx = 0, dy = 0;
        switch (player.getDirection()) {
            case UP:
                dy = -1;
                break;
            case DOWN:
                dy = 1;
                break;
            case LEFT:
                dx = -1;
                break;
            case RIGHT:
                dx = 1;
                break;
        }

        // check bounds prior to committing to move the player
        player.setPositionDelta(dx, dy);
        if ((player.getX() < 0 || player.getX() > width * this.tileSize) ||
                (player.getY() < 0 || player.getY() > height * this.tileSize)) {

            // rollback the movement
            player.setPositionDelta(dx * -1, dy * -1);
            return false;
        }

        state.addChangedPlayer(player);
        return true;
    }
}
