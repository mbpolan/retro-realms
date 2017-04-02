package com.mbpolan.retrorealms.services.map;

import java.util.List;

/**
 * Descriptor for the entire game map.
 *
 * @author mbpolan
 */
public class GameMap {

    private int width;
    private int height;
    private int tileSize;
    private TilesetMetadata tileMetadata;
    private List<Layer> layers;
    private List<Area> areas;
    private List<Door> doors;

    public static Builder builder() {
        return new Builder();
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

    public TilesetMetadata getTileMetadata() {
        return tileMetadata;
    }

    public Tile createEmptyTile() {
        return new Tile(tileMetadata.getFirstId() - 1, null);
    }

    public List<Layer> getLayers() {
        return layers;
    }

    public List<Area> getAreas() {
        return areas;
    }

    public List<Door> getDoors() {
        return doors;
    }

    public static final class Builder {
        private int width;
        private int height;
        private int tileSize;
        private TilesetMetadata tileMetadata;
        private List<Layer> layers;
        private List<Area> areas;
        private List<Door> doors;

        private Builder() {
        }

        public static Builder aGameMap() {
            return new Builder();
        }

        public Builder width(int width) {
            this.width = width;
            return this;
        }

        public Builder height(int height) {
            this.height = height;
            return this;
        }

        public Builder tileSize(int tileSize) {
            this.tileSize = tileSize;
            return this;
        }

        public Builder tileMetadata(TilesetMetadata tileMetadata) {
            this.tileMetadata = tileMetadata;
            return this;
        }

        public Builder layers(List<Layer> layers) {
            this.layers = layers;
            return this;
        }

        public Builder areas(List<Area> areas) {
            this.areas = areas;
            return this;
        }

        public Builder doors(List<Door> doors) {
            this.doors = doors;
            return this;
        }

        public GameMap build() {
            GameMap gameMap = new GameMap();
            gameMap.width = this.width;
            gameMap.height = this.height;
            gameMap.tileSize = this.tileSize;
            gameMap.tileMetadata = this.tileMetadata;
            gameMap.doors = this.doors;
            gameMap.layers = this.layers;
            gameMap.areas = this.areas;
            return gameMap;
        }
    }
}
