package com.mbpolan.retrorealms.services;

import com.mbpolan.retrorealms.beans.responses.GameStateResponse;
import com.mbpolan.retrorealms.beans.responses.LoginResponse;
import com.mbpolan.retrorealms.beans.responses.MapInfoResponse;
import com.mbpolan.retrorealms.services.beans.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

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

    /**
     * Scheduled task that sends out updated game states to players.
     */
    @Scheduled(fixedDelay = 100)
    public synchronized void gameStateDispatcher() {
        // compute the current state of the game
        GameStateResponse gameState = new GameStateResponse();

        // and send it to each connected player
        players.values().forEach(p -> p.send(gameState));
    }

    /**
     * Adds a player to the game.
     *
     * @param sessionId The player's web socket session ID.
     * @param username The player's username.
     */
    public synchronized void addPlayer(String sessionId, String username) {
        if (players.containsKey(username)) {
            throw new IllegalStateException(String.format("Player already exists: %s", username));
        }

        Player player = new Player(sessionId, username, socket);
        players.put(username, player);

        // tell the player their login was successful
        player.send(new LoginResponse(true));

        // send the player map information
        player.send(new MapInfoResponse(10, 10, Stream.generate(() -> 1)
                .limit(100)
                .mapToInt(Integer::new)
                .toArray()));
    }

    /**
     * Handler invoked when a web socket session has terminated.
     *
     * @param event The application event.
     */
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
