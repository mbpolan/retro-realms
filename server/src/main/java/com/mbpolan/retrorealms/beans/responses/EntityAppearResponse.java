package com.mbpolan.retrorealms.beans.responses;

import com.mbpolan.retrorealms.beans.responses.data.PlayerInfo;

/**
 * @author Mike Polan
 */
public class EntityAppearResponse extends AbstractResponse {

    private PlayerInfo player;

    public EntityAppearResponse(PlayerInfo player) {
        super("entityAppear");

        this.player = player;
    }

    public PlayerInfo getPlayer() {
        return player;
    }
}
