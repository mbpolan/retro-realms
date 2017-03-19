package com.mbpolan.retrorealms.services;

import com.mbpolan.retrorealms.beans.responses.*;
import com.mbpolan.retrorealms.beans.responses.data.PlayerInfo;
import com.mbpolan.retrorealms.services.beans.Direction;
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
import java.util.stream.Collectors;

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

    // map of all players in the game now, keyed by their session IDs
    private Map<String, Player> players;
    private volatile int lastPlayerId = 0;

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
     * @param sessionId The player's websocket session ID.
     * @param username The player's username.
     */
    public synchronized void addPlayer(String sessionId, String username) {
        if (players.containsKey(sessionId)) {
            throw new IllegalStateException(String.format("Player already exists: %s", sessionId));
        }

        // create a new player and put them in the global player map
        Player player = new Player(lastPlayerId++, sessionId, username, "char1", Direction.DOWN, socket);
        players.put(sessionId, player);

        // tell the player their login was successful
        player.send(new LoginResponse(player.getId(), true));

        // add the player to the map area
        MapArea area = map.getMapArea(player.getMapArea());
        area.addPlayer(player);

        // and send the player their initial map update
        List<Integer> tileIds = area.getTileIds();
        List<PlayerInfo> playerInfos = area.getPlayers().stream()
                .map(p -> new PlayerInfo(
                        p.getId(),
                        p.getUsername(),
                        p.getSprite(),
                        p.getX(),
                        p.getY(),
                        p.getDirection().getValue()))
                .collect(Collectors.toList());

        player.send(new MapInfoResponse(area.getWidth(), area.getHeight(), tileIds, playerInfos));
    }

    /**
     * Initiates the player walking from their current position on the map.
     *
     * @param sessionId The player's websocket session ID.
     * @param direction The direction to move the player.
     */
    public synchronized void movePlayer(String sessionId, Direction direction) {
        Player player = players.get(sessionId);

        // have the player start moving if they aren't already
        if (!player.isMoving()) {
            player.setMoving(true);
            player.setDirection(direction);

            // notify all spectators
            map.getMapArea(player.getMapArea()).sendToAll(
                    new EntityMoveStartResponse(player.getId(), player.getDirection().getValue()));
        }
    }

    /**
     * Halts a moving player from walking in their current direction.
     *
     * @param sessionId The player's websocket session ID.
     */
    public synchronized void stopPlayer(String sessionId) {
        Player player = players.get(sessionId);

        // have the player stop moving immediately if they haven't already
        if (player.isMoving()) {
            player.setMoving(false);

            // notify all spectators
            map.getMapArea(player.getMapArea()).sendToAll(new EntityMoveStopResponse(player.getId()));
        }
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
