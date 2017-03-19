package com.mbpolan.retrorealms.beans.responses.data;

/**
 * @author Mike Polan
 */
public class PlayerInfo {

    private int id;
    private String username;
    private String sprite;
    private int x;
    private int y;

    public PlayerInfo(int id, String username, String sprite, int x, int y) {
        this.id = id;
        this.username = username;
        this.sprite = sprite;
        this.x = x;
        this.y = y;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getSprite() {
        return sprite;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
