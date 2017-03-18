package com.mbpolan.retrorealms.services;

import com.mbpolan.retrorealms.beans.responses.LoginResponse;
import com.mbpolan.retrorealms.services.beans.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * Core service that manages the state of the game.
 *
 * @author Mike Polan
 */
@Service
public class GameService implements ApplicationListener<SessionDisconnectEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(GameService.class);

    private Map<String, Player> players;

    @Autowired
    private SimpMessagingTemplate socket;

    @PostConstruct
    public void init() {
        this.players = new HashMap<>();
    }

    public synchronized void addPlayer(String sessionId, String username) {
        if (players.containsKey(username)) {
            throw new IllegalStateException(String.format("Player already exists: %s", username));
        }

        Player player = new Player(sessionId, username, socket);
        players.put(username, player);

        player.send(new LoginResponse(true));
    }

    @Override
    public synchronized void onApplicationEvent(SessionDisconnectEvent event) {
        StompHeaderAccessor stomp = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = stomp.getSessionId();

        LOG.debug("User with session {} disconnected", sessionId);
        players.values().stream()
                .filter(p -> p.getSessionId().equals(sessionId))
                .findFirst()
                .ifPresent(p -> players.remove(p.getUsername()));
    }
}
