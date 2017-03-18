package com.mbpolan.retrorealms.services;

import com.mbpolan.retrorealms.beans.responses.GameStateResponse;
import com.mbpolan.retrorealms.beans.responses.LoginResponse;
import com.mbpolan.retrorealms.beans.responses.MapInfoResponse;
import com.mbpolan.retrorealms.services.beans.MapArea;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Core service that manages the state of the game.
 *
 * @author Mike Polan
 */
@Service
public class GameService implements ApplicationListener<SessionDisconnectEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(GameService.class);

    @Autowired
    private MapService map;

    @Autowired
    private SimpMessagingTemplate socket;

    // map of all players in the game now, keyed by their usernames
    private Map<String, Player> players;

    @PostConstruct
    public void init() throws IOException {
        this.players = new HashMap<>();
    }

    /**
     * Scheduled task that sends out updated game states to players.
     */
    @Scheduled(fixedDelay = 100)
    public synchronized void gameStateDispatcher() {
        // recompute the state of each map area
        map.getMapAreas().forEach(a -> {
            if (a.updateState()) {
                // TODO populate this with relevant info
                GameStateResponse gameState = new GameStateResponse();

                // if the state has changed, notify all the players in that area only
                a.getPlayers().forEach(p -> p.send(gameState));
            }
        });
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

        // create a new player and put them in the global player map
        Player player = new Player(sessionId, username, socket);
        players.put(username, player);

        // tell the player their login was successful
        player.send(new LoginResponse(true));

        // add the player to the map area they last logged out from
        MapArea area = map.getMapArea(player.getMapArea());
        area.addPlayer(player);

        // send the player their first map update
        List<Integer> tiles = new ArrayList<>();
        area.getTiles().forEach(row -> row.forEach(col -> tiles.add(col.getId())));
        player.send(new MapInfoResponse(area.getWidth(), area.getHeight(), tiles));
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
                .ifPresent(p -> {
                    // remove the player from his map area and from the global player map
                    map.getMapArea(p.getMapArea()).removePlayer(p);
                    players.remove(p.getUsername());
                });
    }
}
