package com.mbpolan.retrorealms.services;

import com.mbpolan.retrorealms.beans.responses.*;
import com.mbpolan.retrorealms.beans.responses.data.LoginResult;
import com.mbpolan.retrorealms.beans.responses.data.PlayerInfo;
import com.mbpolan.retrorealms.repositories.entities.UserAccount;
import com.mbpolan.retrorealms.services.beans.Direction;
import com.mbpolan.retrorealms.services.beans.GameState;
import com.mbpolan.retrorealms.services.beans.MapArea;
import com.mbpolan.retrorealms.services.beans.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Core service that manages the state of the game.
 *
 * @author mbpolan
 */
@Service
public class GameService implements ApplicationListener<SessionDisconnectEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(GameService.class);

    @Autowired
    private MapService map;

    @Autowired
    private SettingsService settings;

    @Autowired
    private TaskScheduler scheduler;

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
    @Scheduled(fixedDelay = 200)
    public void gameStateDispatcher() {
        // recompute the state of each map area
        map.getMapAreas().forEach(a -> {
            a.lock();

            GameState state = a.popState();
            if (state != null) {
                // compute an updated game state for this area
                GameStateResponse gameState = new GameStateResponse(
                        state.getPlayers().stream()
                                .map(p -> new PlayerInfo(p.getId(), null, null, p.plane().getX1(), p.plane().getY1(), null))
                                .collect(Collectors.toList()));

                // if the state has changed, notify all the players in that area only
                a.sendToAll(gameState);
            }

            a.unlock();
        });
    }

    /**
     * Rejects a player from the game.
     *
     * @param sessionId The session ID of the player.
     */
    public void rejectPlayer(String sessionId) {
        new Player(0, sessionId, null, null, Direction.DOWN, socket)
                .send(LoginResponse.createFailure(LoginResult.INVALID_LOGIN));
    }

    /**
     * Adds a player to the game.
     *
     * @param sessionId The player's websocket session ID.
     * @param account Data about the user's account.
     * @return true if the player was successfully added to the game, false otherwise.
     */
    public synchronized boolean addPlayer(String sessionId, UserAccount account) {
        if (players.containsKey(sessionId)) {
            LOG.error("Player session already exists: {}");
            return false;
        }

        // create a new player and put them in the global player map
        Player player = new Player(lastPlayerId++, sessionId, account.getUsername(), account.getSprite(),
                Direction.fromValue(account.getDirection()), socket);
        player.setAbsolutePosition(account.getMapArea(), account.getX(), account.getY());
        players.put(sessionId, player);

        // tell the player their login was successful
        player.send(LoginResponse.createSuccess(player.getId()));

        // add the player to the map area
        MapArea area = map.getMapArea(player.getMapArea());
        area.lock();
        area.addPlayer(player);

        // and send the player their initial map update
        List<Integer> tileIds = area.getTileIds();
        List<PlayerInfo> playerInfos = area.getPlayers().stream()
                .map(p -> new PlayerInfo(
                        p.getId(),
                        p.getUsername(),
                        p.getSprite(),
                        p.plane().getX1(),
                        p.plane().getY2(),
                        p.getDirection().getValue()))
                .collect(Collectors.toList());

        player.send(new MapInfoResponse(area.getWidth(), area.getHeight(), tileIds, playerInfos));

        // notify spectators that this player has appeared
        area.sendToAll(new EntityAppearResponse(new PlayerInfo(
                player.getId(),
                player.getUsername(),
                player.getSprite(),
                player.plane().getX1(),
                player.plane().getY1(),
                player.getDirection().getValue())), player);

        area.unlock();

        return true;
    }

    /**
     * Initiates the player walking from their current position on the map.
     *
     * @param sessionId The player's websocket session ID.
     * @param direction The direction to move the player.
     */
    public synchronized void movePlayer(String sessionId, Direction direction) {
        Player player = players.get(sessionId);
        long now = System.currentTimeMillis();

        // have the player start moving if they aren't already, and if they haven't moved "recently"
        if (!player.isMoving() && now - player.getLastMovement() >= settings.getPlayerWalkDelay()) {
            MapArea area = map.getMapArea(player.getMapArea());

            // test if the player can move, and if so, schedule their next movement
            area.lock();
            if (area.canPlayerMove(player, direction)) {
                player.setLastMovement(now);
                player.setMoving(true);
                player.setDirection(direction);

                // notify all spectators
                area.sendToAll(new EntityMoveStartResponse(player.getId(), player.getDirection().getValue()));

                // schedule the movement immediately
                scheduleWithDelay(() -> onMovePlayer(player), settings.getPlayerWalkDelay());
            }

            else {
                onStopPlayerInArea(player, area);
            }

            area.unlock();
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
            onStopPlayer(player);
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
                    MapArea area = map.getMapArea(p.getMapArea());
                    area.lock();

                    // remove the player from his map area and from the global player map
                    area.removePlayer(p);
                    players.remove(p.getUsername());

                    // notify spectators that this player has disappeared
                    area.sendToAll(new EntityDisappearResponse(p.getId()));

                    area.unlock();
                });
    }

    /**
     * Scheduled task for moving a player to another position.
     *
     * @param player The moving player.
     */
    private void onMovePlayer(Player player) {
        // attempt to move the player, and if successful, schedule their next movement afterwards
        if (player.isMoving()) {
            player.setLastMovement(System.currentTimeMillis());

            MapArea area = map.getMapArea(player.getMapArea());
            area.lock();

            if (area.movePlayer(player)) {
                scheduleWithDelay(() -> onMovePlayer(player), settings.getPlayerWalkDelay());
            }

            else {
                onStopPlayerInArea(player, area);
            }

            area.unlock();
        }

        // otherwise stop moving the player and notify spectators
        else {
            onStopPlayer(player);
        }
    }

    /**
     * Stops a moving player from any further movements.
     *
     * @param player The player to stop moving.
     */
    private void onStopPlayer(Player player) {
        // notify spectators that this player is no longer moving
        MapArea area = map.getMapArea(player.getMapArea());

        area.lock();
        onStopPlayerInArea(player, area);
        area.unlock();
    }

    /**
     * Stops a moving player in a given area from further movements.
     *
     * It is assumed that the {@link MapArea} is locked before this method is invoked.
     *
     * @param player The player to stop moving.
     * @param area The map area in which the player is moving.
     */
    private void onStopPlayerInArea(Player player, MapArea area) {
        player.setMoving(false);
        area.sendToAll(new EntityMoveStopResponse(player.getId(), player.plane().getX1(), player.plane().getY1()));
    }

    /**
     * Convenience method to schedule a task that will be executed as soon as possible.
     *
     * @param task The task to schedule.
     */
    private void scheduleNow(Runnable task) {
        scheduler.schedule(task, Date.from(Instant.now()));
    }

    /**
     * Convenience method to schedule a task that will be executed after some milliseconds.
     *
     * @param task The task to schedule.
     * @param delayMs The delay before executing the task.
     */
    private void scheduleWithDelay(Runnable task, long delayMs) {
        scheduler.schedule(task, Date.from(Instant.now().plus(delayMs, ChronoUnit.MILLIS)));
    }
}
