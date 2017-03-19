package com.mbpolan.retrorealms.beans.responses;

import com.mbpolan.retrorealms.beans.responses.data.PlayerInfo;

import java.util.List;

/**
 * @author Mike Polan
 */
public class MapInfoResponse extends AbstractResponse {

    private int width;
    private int height;
    private List<Integer> tiles;
    private List<PlayerInfo> players;

    public MapInfoResponse(int width, int height, List<Integer> tiles, List<PlayerInfo> players) {
        super("mapInfo");
        this.width = width;
        this.height = height;
        this.tiles = tiles;
        this.players = players;
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

    public List<PlayerInfo> getPlayers() {
        return players;
    }
}
