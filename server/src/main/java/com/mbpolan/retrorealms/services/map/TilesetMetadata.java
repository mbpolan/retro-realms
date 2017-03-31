package com.mbpolan.retrorealms.services.map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Descriptor for a set of tile metadata.
 *
 * @author mbpolan
 */
public class TilesetMetadata {

    private static final Logger LOG = LoggerFactory.getLogger(TilesetMetadata.class);

    private int firstId;
    private Map<Integer, Tile> tiles;

    public TilesetMetadata(Map<Integer, Tile> tiles) {
        this.firstId = 0;
        this.tiles = tiles;
    }

    /**
     * Returns the ID of the first tile in the tileset.
     *
     * @return The ID of the tile.
     */
    public int getFirstId() {
        return firstId;
    }

    /**
     * Sets the ID of the first tile in the tileset.
     *
     * @param firstId The ID of the tile.
     */
    public void setFirstId(int firstId) {
        this.firstId = firstId;
    }

    /**
     * Returns a {@link Tile} descriptor for a tile identified by an ID number.
     *
     * @param id The ID number.
     * @return The tile descriptor, or null if not found.
     */
    public Tile get(int id) {
        if (!tiles.containsKey(id) && id >= firstId) {
            LOG.warn("Cannot find tile in metadata with ID {}", id);
        }

        return tiles.get(id);
    }
}
