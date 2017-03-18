package com.mbpolan.retrorealms.beans.responses;

import java.util.List;

/**
 * @author Mike Polan
 */
public class MapInfoResponse extends AbstractResponse {

    private int width;
    private int height;
    private List<Integer> tiles;

    public MapInfoResponse(int width, int height, List<Integer> tiles) {
        super("mapInfo");
        this.width = width;
        this.height = height;
        this.tiles = tiles;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public List<Integer> getTiles() {
        return tiles;
    }
}
