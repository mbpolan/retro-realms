package com.mbpolan.retrorealms.services;

import com.mbpolan.retrorealms.repositories.UserAccountRepository;
import com.mbpolan.retrorealms.repositories.entities.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Optional;

/**
 * Service that handles incoming authentication requests from clients.
 *
 * @author mbpolan
 */
@Service
public class AuthService {

    @Autowired
    private UserAccountRepository users;

    /**
     * Authenticates a username and password credential against the backend database.
     *
     * @param username The username.
     * @param password The password.
     * @return A {@link UserAccount} bean if the credentials are valid.
     */
    public UserAccount authenticate(String username, String password) {
        return users.findByUsernameAndPassword(username, password);
    }

    /**
     * Updates the last login timestamp for a user account.
     *
     * @param account The user account.
     */
    public void updateLastLogin(UserAccount account) {
        account.setLastLogin(new Timestamp(System.currentTimeMillis()));
        users.save(account);
    }
}
