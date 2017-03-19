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
    private GameState state;

    /**
     * Creates a map area with tiles and geometry.
     *
     * The initial game state contains no players.
     *
     * @param tiles The rectangle of tiles in this area, in row-major order.
     * @param width The width of the map area, in tiles.
     * @param height The height of the map area, in tiles.
     */
    public MapArea(List<List<Tile>> tiles, int width, int height) {
        this.players = new ArrayList<>();
        this.tiles = tiles;
        this.width = width;
        this.height = height;
        this.state = new GameState(this.players);
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
     * @return true if this map area has changed, false if not.
     */
    public boolean updateState() {
        GameState newState = new GameState(this.players);

        // has the game state changed?
        boolean changed = !newState.equals(this.state);
        if (changed) {
            this.state = newState;
        }

        return changed;
    }
}
