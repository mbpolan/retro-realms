package com.mbpolan.retrorealms.beans.responses;

/**
 * Bean that contains a response for a login request.
 *
 * @author mbpolan
 */
public class LoginResponse extends AbstractResponse {

    private final int id;
    private boolean success;

    public LoginResponse(int id, boolean success) {
        super("login");
        this.id = id;
        this.success = success;
    }

    public int getId() {
        return id;
    }

    public boolean isSuccess() {
        return success;
    }
}
