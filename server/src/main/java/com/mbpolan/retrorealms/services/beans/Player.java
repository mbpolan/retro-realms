package com.mbpolan.retrorealms.services.beans;

import com.mbpolan.retrorealms.beans.responses.AbstractResponse;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;

/**
 * Representation of a player that's logged into the game.
 *
 * @author Mike Polan
 */
public class Player {

    private int id;
    private String sessionId;
    private String username;
    private String sprite;
    private int mapArea;
    private int x;
    private int y;
    private int speed;
    private boolean moving;
    private long lastMovement;
    private Direction direction;
    private SimpMessagingTemplate socket;

    /**
     * Creates a new player descriptor.
     *
     * @param id The unique ID assigned to this player.
     * @param sessionId The player's websocket session ID.
     * @param username The player's username.
     * @param sprite The name of the sprite for the player.
     * @param direction The direction the player is initially facing.
     * @param socket The websocket to communicate over.
     */
    public Player(int id, String sessionId, String username, String sprite, Direction direction, SimpMessagingTemplate socket) {
        this.id = id;
        this.sessionId = sessionId;
        this.username = username;
        this.sprite = sprite;
        this.socket = socket;
        this.mapArea = 0;
        this.x = 0;
        this.y = 0;
        this.speed = 8;
        this.moving = false;
        this.lastMovement = 0;
        this.direction = direction;
    }

    public int getId() {
        return id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getUsername() {
        return username;
    }

    public String getSprite() {
        return sprite;
    }

    public int getMapArea() {
        return mapArea;
    }

    public void setPositionDelta(int dx, int dy) {
        this.x += dx;
        this.y += dy;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getSpeed() {
        return speed;
    }

    public boolean isMoving() {
        return moving;
    }

    public void setMoving(boolean moving) {
        this.moving = moving;
    }

    public long getLastMovement() {
        return lastMovement;
    }

    public void setLastMovement(long lastMovement) {
        this.lastMovement = lastMovement;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    /**
     * Sends a message to the player.
     *
     * The message will be dispatched only to the player in question; it will not be broadcast to any
     * other player in the game.
     *
     * @param message The message to send.
     */
    public void send(AbstractResponse message) {
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headers.setSessionId(sessionId);
        headers.setLeaveMutable(true);

        socket.convertAndSendToUser(sessionId, "/queue/game", message, headers.getMessageHeaders());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Player player = (Player) o;

        if (mapArea != player.mapArea) return false;
        if (x != player.x) return false;
        if (y != player.y) return false;
        if (!sessionId.equals(player.sessionId)) return false;
        return username.equals(player.username);

    }

    @Override
    public int hashCode() {
        int result = sessionId.hashCode();
        result = 31 * result + username.hashCode();
        result = 31 * result + mapArea;
        result = 31 * result + x;
        result = 31 * result + y;
        return result;
    }
}
