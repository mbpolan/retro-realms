package com.mbpolan.retrorealms.services.beans;

import com.mbpolan.retrorealms.beans.responses.AbstractResponse;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;

/**
 * Representation of a player that's logged into the game.
 *
 * @author mbpolan
 */
public class Player {

    private int id;
    private String sessionId;
    private String username;
    private String sprite;
    private int mapArea;
    private Rectangle plane;
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
        this.speed = 8;
        this.moving = false;
        this.lastMovement = 0;
        this.direction = direction;
        this.plane = new Rectangle();
        this.setAbsolutePosition(0, 0, 0);
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

    public void setAbsolutePosition(int mapArea, int x, int y) {
        this.mapArea = mapArea;
        this.plane.setX1(x);
        this.plane.setX2(x + 32); // FIXME
        this.plane.setY1(y);
        this.plane.setY2(y + 32); // FIXME
    }

    public void setPositionDelta(int dx, int dy) {
        this.plane.translate(dx, dy);
    }

    public Rectangle plane() {
        return plane;
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
}
