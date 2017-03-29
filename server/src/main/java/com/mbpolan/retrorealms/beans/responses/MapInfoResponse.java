package com.mbpolan.retrorealms.beans.responses;

import com.mbpolan.retrorealms.beans.responses.data.PlayerInfo;

import java.util.List;

/**
 * @author mbpolan
 */
public class MapInfoResponse extends AbstractResponse {

    private int width;
    private int height;
    private List<List<Integer>> layers;
    private List<PlayerInfo> players;

    public MapInfoResponse(int width, int height, List<List<Integer>> layers, List<PlayerInfo> players) {
        super("mapInfo");
        this.width = width;
        this.height = height;
        this.layers = layers;
        this.players = players;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public List<List<Integer>> getLayers() {
        return layers;
    }

    public List<PlayerInfo> getPlayers() {
        return players;
    }
}
