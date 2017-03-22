package com.mbpolan.retrorealms.beans.responses;

import com.mbpolan.retrorealms.beans.responses.data.PlayerInfo;

import java.util.List;

/**
 * @author mbpolan
 */
public class GameStateResponse extends AbstractResponse {

    private List<PlayerInfo> players;

    public GameStateResponse(List<PlayerInfo> players) {
        super("gameState");

        this.players = players;
    }

    public List<PlayerInfo> getPlayers() {
        return players;
    }
}
