package com.mbpolan.retrorealms.services.beans;

import com.mbpolan.retrorealms.beans.responses.AbstractResponse;
import org.springframework.messaging.simp.SimpMessagingTemplate;

/**
 * @author Mike Polan
 */
public class Player {

    private String username;
    private SimpMessagingTemplate socket;

    public Player(String username, SimpMessagingTemplate socket) {
        this.username = username;
        this.socket = socket;
    }

    public void send(AbstractResponse message) {
//        socket.convertAndSendToUser();
    }
}
