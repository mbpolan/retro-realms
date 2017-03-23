package com.mbpolan.retrorealms.settings;

/**
 * Bean that contains settings for an asset.
 *
 * @author mbpolan
 */
public class AssetSettings {

    private String name;
    private String path;
    private String resource;

    public AssetSettings(String name, String path, String resource) {
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
