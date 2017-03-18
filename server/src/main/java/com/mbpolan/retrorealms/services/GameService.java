package com.mbpolan.retrorealms.services;

import com.mbpolan.retrorealms.services.beans.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * Core service that manages the state of the game.
 *
 * @author Mike Polan
 */
@Service
public class GameService {

    private Map<String, Player> players;

    @Autowired
    private SimpMessagingTemplate socket;

    @PostConstruct
    public void init() {
        this.players = new HashMap<>();
    }

    public void addPlayer(String username) {
        if (players.containsKey(username)) {
            throw new IllegalStateException(String.format("Player already exists: %s", username));
        }

        Player player = players.put(username, new Player(username, socket));

    }
}
