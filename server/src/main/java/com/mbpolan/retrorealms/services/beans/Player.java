package com.mbpolan.retrorealms.services.beans;

import com.mbpolan.retrorealms.beans.responses.AbstractResponse;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;

/**
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
    private Direction direction;
    private SimpMessagingTemplate socket;

    public Player(int id, String sessionId, String username, String sprite, Direction direction, SimpMessagingTemplate socket) {
        this.id = id;
        this.sessionId = sessionId;
        this.username = username;
        this.sprite = sprite;
        this.socket = socket;
        this.mapArea = 0;
        this.x = 0;
        this.y = 0;
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

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

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
