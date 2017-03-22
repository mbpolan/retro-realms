package com.mbpolan.retrorealms.beans.responses.data;

/**
 * Bean that contains information about a player on the map.
 *
 * @author mbpolan
 */
public class PlayerInfo {

    private int id;
    private String username;
    private String sprite;
    private int x;
    private int y;
    private String dir;

    /**
     * Creates a new player information bean.
     *
     * @param id The ID number assigned to the player.
     * @param username The player's username.
     * @param sprite The name of the sprite.
     * @param x The player's x-coordinate.
     * @param y The player's y-coordinate.
     * @param dir The direction the player is facing.
     */
    public PlayerInfo(int id, String username, String sprite, int x, int y, String dir) {
        this.id = id;
        this.username = username;
        this.sprite = sprite;
        this.x = x;
        this.y = y;
        this.dir = dir;
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

    public String getDir() {
        return dir;
    }
}
