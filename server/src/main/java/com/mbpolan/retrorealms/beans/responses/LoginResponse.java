package com.mbpolan.retrorealms.beans.responses;

/**
 * Bean that contains a response for a login request.
 *
 * @author Mike Polan
 */
public class LoginResponse extends AbstractResponse {

    private boolean success;

    public LoginResponse(boolean success) {
        super("login");
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}
