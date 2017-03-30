package com.mbpolan.retrorealms.beans.info;

/**
 * Bean that contains information about assets and other game data about this server.
 *
 * @author mbpolan
 */
public class ServerInfo {

    private int tileSize;
    private TilesetMetadataInfo tileset;
    private SpritesMetadataInfo sprites;

    public ServerInfo(int tileSize, TilesetMetadataInfo tileset, SpritesMetadataInfo sprites) {
        this.tileSize = tileSize;
        this.tileset = tileset;
        this.sprites = sprites;
    }

    public int getTileSize() {
        return tileSize;
    }

    public TilesetMetadataInfo getTileset() {
        return tileset;
    }

    public SpritesMetadataInfo getSprites() {
        return sprites;
    }
}
