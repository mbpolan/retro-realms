package com.mbpolan.retrorealms.beans.info;

import java.util.List;

/**
 * Bean that provides information about the tilesets in use by this server.
 *
 * @author mbpolan
 */
public class TilesetMetadataInfo {

    private String name;
    private String resource;
    private List<TileMetadataInfo> tiles;

    public TilesetMetadataInfo(String name, String resource, List<TileMetadataInfo> tiles) {
        this.name = name;
        this.resource = resource;
        this.tiles = tiles;
    }

    public String getName() {
        return name;
    }

    public String getResource() {
        return resource;
    }

    public List<TileMetadataInfo> getTiles() {
        return tiles;
    }
}
