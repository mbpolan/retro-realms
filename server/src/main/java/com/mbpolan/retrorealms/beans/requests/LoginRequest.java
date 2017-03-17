package com.mbpolan.retrorealms.beans.requests;

/**
 * Message that contains a user's login request.
 *
 * @author Mike Polan
 */
public class LoginRequest extends AbstractRequest {

    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
