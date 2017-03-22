package com.mbpolan.retrorealms.controllers;

import com.mbpolan.retrorealms.beans.requests.*;
import com.mbpolan.retrorealms.repositories.entities.UserAccount;
import com.mbpolan.retrorealms.services.AuthService;
import com.mbpolan.retrorealms.services.GameService;
import com.mbpolan.retrorealms.services.beans.Direction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

/**
 * @author mbpolan
 */
@Controller
public class GameController {

    private static final Logger LOG = LoggerFactory.getLogger(GameController.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private GameService gameService;

    /**
     * WebSocket subscription handler for all game-related requests.
     *
     * @param request The incoming request.
     * @param headers The SIMP message headers.
     */
    @SubscribeMapping("/game")
    public void handleRequest(AbstractRequest request, SimpMessageHeaderAccessor headers) {
        String sessionId = headers.getSessionId();

        // figure out what kind of message this is based on the header identifier
        switch (request.getHeader()) {
            case RequestHeader.LOGIN:
                handleLogin(sessionId, (LoginRequest) request);
                break;

            case RequestHeader.MOVE_START:
                handleMoveStart(sessionId, (MoveStartRequest) request);
                break;

            case RequestHeader.MOVE_STOP:
                handleMoveStop(sessionId, (MoveStopRequest) request);
                break;

            default:
                LOG.debug("Unknown request header: {}", request.getHeader());
                break;
        }
    }

    /**
     * Handles a player login request.
     *
     * @param sessionId The player's websocket session ID.
     * @param request The request payload.
     */
    private void handleLogin(String sessionId, LoginRequest request) {
        UserAccount account = authService.authenticate(request.getUsername(), request.getPassword());
        if (account != null) {
            if (gameService.addPlayer(sessionId, account)) {
                authService.updateLastLogin(account);
            }
        }

        else {
            gameService.rejectPlayer(sessionId);
        }
    }

    /**
     * Handles a player login movement start request.
     *
     * @param sessionId The player's websocket session ID.
     * @param request The request payload.
     */
    private void handleMoveStart(String sessionId, MoveStartRequest request) {
        Direction direction = Direction.fromValue(request.getDir());

        gameService.movePlayer(sessionId, direction);
    }

    /**
     * Handles a player movement stop request.
     *
     * @param sessionId The player's websocket session ID.
     * @param request The request payload.
     */
    private void handleMoveStop(String sessionId, MoveStopRequest request) {
        gameService.stopPlayer(sessionId);
    }
}
