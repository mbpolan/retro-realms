package com.mbpolan.retrorealms.beans.info;

/**
 * Bean that provides information about the sprite sheets in use by this server.
 *
 * @author mbpolan
 */
public class SpritesMetadataInfo {

    private String name;
    private String path;
    private String resource;

    public SpritesMetadataInfo(String name, String path, String resource) {
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
