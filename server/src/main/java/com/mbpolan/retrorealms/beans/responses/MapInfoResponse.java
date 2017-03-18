package com.mbpolan.retrorealms.beans.responses;

/**
 * @author Mike Polan
 */
public class MapInfoResponse extends AbstractResponse {

    private int width;
    private int height;
    private int[] tiles;

    public MapInfoResponse(int width, int height, int[] tiles) {
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

    public int[] getTiles() {
        return tiles;
    }
}
