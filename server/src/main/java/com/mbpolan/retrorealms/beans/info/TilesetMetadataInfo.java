package com.mbpolan.retrorealms.beans.info;

/**
 * Bean that provides information about the tilesets in use by this server.
 *
 * @author mbpolan
 */
public class TilesetMetadataInfo {

    private String path;
    private String resource;

    public TilesetMetadataInfo(String path, String resource) {
        this.path = path;
        this.resource = resource;
    }

    public String getPath() {
        return path;
    }

    public String getResource() {
        return resource;
    }
}
