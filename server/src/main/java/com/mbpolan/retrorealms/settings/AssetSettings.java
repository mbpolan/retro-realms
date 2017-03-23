package com.mbpolan.retrorealms.settings;

/**
 * Bean that contains settings for an asset.
 *
 * @author mbpolan
 */
public class AssetSettings {

    private String path;
    private String resource;

    public AssetSettings(String path, String resource) {
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
