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

    public MapSettings(int width, int height, int tileSize, String file) {
        this.width = width;
        this.height = height;
        this.tileSize = tileSize;
        this.file = file;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getTileSize() {
        return tileSize;
    }

    public void setTileSize(int tileSize) {
        this.tileSize = tileSize;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
