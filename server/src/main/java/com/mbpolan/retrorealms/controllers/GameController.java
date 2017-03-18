package com.mbpolan.retrorealms.controllers;

import com.mbpolan.retrorealms.beans.requests.LoginRequest;
import com.mbpolan.retrorealms.beans.responses.LoginResponse;
import com.mbpolan.retrorealms.services.AuthService;
import com.mbpolan.retrorealms.services.GameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

/**
 * @author Mike Polan
 */
@Controller
public class GameController {

    private static final Logger LOG = LoggerFactory.getLogger(GameController.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private GameService gameService;

    @Autowired
    private SimpMessagingTemplate socket;

    @MessageMapping("/game")
    @SendTo("/topic/game")
    public LoginResponse login(LoginRequest data) {
//        LOG.debug("{}", principal.getName());

        if (authService.authenticate(data.getUsername(), data.getPassword())) {
            gameService.addPlayer(data.getUsername());
        }
        return new LoginResponse("mike".equals(data.getUsername()));
    }
}
