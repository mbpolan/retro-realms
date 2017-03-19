package com.mbpolan.retrorealms.settings;

/**
 * Settings for how the game server functions.
 *
 * @author Mike Polan
 */
public class GameSettings {

    private PlayerSettings players;

    public GameSettings(PlayerSettings players) {
        this.players = players;
    }

    public PlayerSettings getPlayers() {
        return players;
    }
}
