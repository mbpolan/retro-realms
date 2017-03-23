package com.mbpolan.retrorealms.beans.info;

/**
 * Bean that contains information about assets and other game data about this server.
 *
 * @author mbpolan
 */
public class ServerInfo {

    private TilesetMetadataInfo tileset;
    private SpritesMetadataInfo sprites;

    public ServerInfo(TilesetMetadataInfo tileset, SpritesMetadataInfo sprites) {
        this.tileset = tileset;
        this.sprites = sprites;
    }

    public TilesetMetadataInfo getTileset() {
        return tileset;
    }

    public SpritesMetadataInfo getSprites() {
        return sprites;
    }
}
