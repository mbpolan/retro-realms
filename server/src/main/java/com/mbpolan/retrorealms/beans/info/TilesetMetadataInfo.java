package com.mbpolan.retrorealms.beans.info;

import java.util.List;

/**
 * Bean that provides information about the tilesets in use by this server.
 *
 * @author mbpolan
 */
public class TilesetMetadataInfo {

    private String name;
    private String path;
    private List<TileMetadataInfo> tiles;

    public TilesetMetadataInfo(String name, String path, List<TileMetadataInfo> tiles) {
        this.name = name;
        this.path = path;
        this.tiles = tiles;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public List<TileMetadataInfo> getTiles() {
        return tiles;
    }
}
