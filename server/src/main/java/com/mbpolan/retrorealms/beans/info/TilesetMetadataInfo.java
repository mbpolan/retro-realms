package com.mbpolan.retrorealms.beans.info;

/**
 * Bean that provides information about the tilesets in use by this server.
 *
 * @author mbpolan
 */
public class TilesetMetadataInfo {

    private String name;
    private String path;
    private String resource;

    public TilesetMetadataInfo(String name, String path, String resource) {
        this.name = name;
        this.path = path;
        this.resource = resource;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getResource() {
        return resource;
    }
}
