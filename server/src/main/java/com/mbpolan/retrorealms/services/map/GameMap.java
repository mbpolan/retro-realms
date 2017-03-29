package com.mbpolan.retrorealms.services.map;

import com.mbpolan.retrorealms.settings.AssetSettings;

import java.util.List;
import java.util.Map;

/**
 * Descriptor for the entire game map.
 *
 * @author mbpolan
 */
public class GameMap {

    private int width;
    private int height;
    private int tileSize;
    private AssetSettings tilesetSettings;
    private TilesetMetadata tileMetadata;
    private List<Layer> layers;

    public GameMap(int width, int height, int tileSize, AssetSettings tilesetSettings,
                   TilesetMetadata tileMetadata, List<Layer> layers) {

        this.width = width;
        this.height = height;
        this.tileSize = tileSize;
        this.tilesetSettings = tilesetSettings;
        this.tileMetadata = tileMetadata;
        this.layers = layers;
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

    public AssetSettings getTilesetSettings() {
        return tilesetSettings;
    }

    public List<Layer> getLayers() {
        return layers;
    }
}
