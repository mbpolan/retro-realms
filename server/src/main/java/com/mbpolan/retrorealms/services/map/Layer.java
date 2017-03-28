package com.mbpolan.retrorealms.services.map;

import java.util.List;

/**
 * Descriptor for a single layer of map tiles.
 *
 * @author mbpolan
 */
public class Layer {

    private List<List<Tile>> tiles;

    public Layer(List<List<Tile>> tiles) {
        this.tiles = tiles;
    }

    public List<List<Tile>> getTiles() {
        return tiles;
    }
}
