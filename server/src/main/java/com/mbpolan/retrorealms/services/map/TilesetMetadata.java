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

    private Map<Integer, Tile> tiles;

    /**
     * Creates a new tileset metadata descriptor.
     *
     * @param tiles The set of tile IDs mapped to their corresponding {@link Tile} descriptors.
     */
    public TilesetMetadata(Map<Integer, Tile> tiles) {
        this.tiles = tiles;
    }

    /**
     * Returns a {@link Tile} descriptor for a tile identified by an ID number.
     *
     * @param id The ID number.
     * @return The tile descriptor, or null if not found.
     */
    public Tile get(int id) {
        if (!tiles.containsKey(id)) {
            LOG.warn("Cannot find tile in metadata with ID {}", id);
            return null;
        }

        return tiles.get(id);
    }
}
