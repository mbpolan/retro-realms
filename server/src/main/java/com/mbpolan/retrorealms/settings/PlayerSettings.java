package com.mbpolan.retrorealms.settings;

/**
 * Settings specific to how players are handled in-game.
 *
 * @author mbpolan
 */
public class PlayerSettings {

    private int walkDelay;
    private int speedMultiplier;

    public PlayerSettings(int walkDelay, int speedMultiplier) {
        this.walkDelay = walkDelay;
        this.speedMultiplier = speedMultiplier;
    }

    public int getWalkDelay() {
        return walkDelay;
    }

    public int getSpeedMultiplier() {
        return speedMultiplier;
    }
}
