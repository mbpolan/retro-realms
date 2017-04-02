package com.mbpolan.retrorealms.services.map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Descriptor for a set of tile metadata.
 *
 * @author mbpolan
 */
public class TilesetMetadata {

    private static final Logger LOG = LoggerFactory.getLogger(TilesetMetadata.class);

    private String name;
    private String imageSourcePath;
    private int firstId;
    private Map<Integer, Tile> tiles;

    /**
     * Creates a new tileset metadata descriptor.
     *
     * @param name The name assigned to this tileset.
     * @param imageSourcePath The static content path on the server where the image for the tileset is available.
     * @param firstId The ID number of the first tile in this tileset.
     * @param tiles A map of tile ID numbers to their descriptors.
     */
    public TilesetMetadata(String name, String imageSourcePath, int firstId, Map<Integer, Tile> tiles) {
        this.name = name;
        this.imageSourcePath = imageSourcePath;
        this.firstId = firstId;
        this.tiles = tiles;
    }

    /**
     * Returns the path on the server where the image for this tileset is available.
     *
     * @return The static content path for the tileset image.
     */
    public String getImageSourcePath() {
        return imageSourcePath;
    }

    /**
     * Returns the name for this tileset.
     *
     * @return The tileset name.
     */
    public String getName() {
        return name;
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
     * Returns a read-only view of all tiles.
     *
     * @return Metadata for all tiles.
     */
    public Collection<Tile> getTiles() {
        return Collections.unmodifiableCollection(tiles.values());
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
