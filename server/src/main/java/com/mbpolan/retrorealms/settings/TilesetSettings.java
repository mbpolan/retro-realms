package com.mbpolan.retrorealms.settings;

/**
 * Bean that contains settings for an asset.
 *
 * @author mbpolan
 */
public class TilesetSettings {

    private String name;
    private String path;

    public TilesetSettings(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }
}
