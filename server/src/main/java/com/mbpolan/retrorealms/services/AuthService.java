package com.mbpolan.retrorealms.services;

import org.springframework.stereotype.Service;

/**
 * Service that handles incoming authentication requests from clients.
 *
 * @author Mike Polan
 */
@Service
public class AuthService {

    public boolean authenticate(String username, String password) {
        // TODO
        return "mike".equals(username);
    }
}
