package com.mbpolan.retrorealms.services.map.assets;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Asset that describes a tileset.
 *
 * @author mbpolan
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TilesetAsset {

    private String name;
    private List<TileAsset> tiles;

    public String getName() {
        return name;
    }

    public List<TileAsset> getTiles() {
        return tiles;
    }
}
