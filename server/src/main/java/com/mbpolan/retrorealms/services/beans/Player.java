package com.mbpolan.retrorealms.services.beans;

import com.mbpolan.retrorealms.beans.responses.AbstractResponse;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;

/**
 * @author Mike Polan
 */
public class Player {

    private String sessionId;
    private String username;
    private SimpMessagingTemplate socket;

    public Player(String sessionId, String username, SimpMessagingTemplate socket) {
        this.sessionId = sessionId;
        this.username = username;
        this.socket = socket;
    }

    public void send(AbstractResponse message) {
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headers.setSessionId(sessionId);
        headers.setLeaveMutable(true);

        socket.convertAndSendToUser(sessionId, "/queue/game", message, headers.getMessageHeaders());
    }
}
