package com.mbpolan.retrorealms.controllers;

import com.mbpolan.retrorealms.beans.requests.AbstractRequest;
import com.mbpolan.retrorealms.beans.requests.LoginRequest;
import com.mbpolan.retrorealms.beans.responses.AbstractResponse;
import com.mbpolan.retrorealms.beans.responses.LoginResponse;
import com.mbpolan.retrorealms.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

/**
 * @author Mike Polan
 */
@Controller
public class GameController {

    @Autowired
    private AuthService authService;

    @MessageMapping("/game")
    @SendTo("/topic/game")
    public AbstractResponse login(LoginRequest data) {
        System.out.println("Got something: " + data);
        return new LoginResponse("mike".equals(data.getUsername()));
    }
}
