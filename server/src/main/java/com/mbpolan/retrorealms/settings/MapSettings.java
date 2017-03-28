package com.mbpolan.retrorealms.settings;

/**
 * Bean that contains general information about the map.
 *
 * @author mbpolan
 */
public class MapSettings {

    private int width;
    private int height;
    private int tileSize;
    private String file;
    private AssetSettings spritesSettings;

    public MapSettings(int width, int height, int tileSize, String file, AssetSettings spritesSettings) {
        this.width = width;
        this.height = height;
        this.tileSize = tileSize;
        this.file = file;
        this.spritesSettings = spritesSettings;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getTileSize() {
        return tileSize;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public AssetSettings getSpritesSettings() {
        return spritesSettings;
    }
}
