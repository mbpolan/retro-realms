package com.mbpolan.retrorealms.settings;

/**
 * Settings specific to how players are handled in-game.
 *
 * @author Mike Polan
 */
public class PlayerSettings {

    private int speedMultipler;

    public PlayerSettings(int speedMultipler) {
        this.speedMultipler = speedMultipler;
    }

    public int getSpeedMultipler() {
        return speedMultipler;
    }
}
