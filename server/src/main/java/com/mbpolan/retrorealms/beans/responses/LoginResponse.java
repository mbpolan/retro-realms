package com.mbpolan.retrorealms.beans.responses;

import com.mbpolan.retrorealms.beans.responses.data.LoginResult;

/**
 * Bean that contains a response for a login request.
 *
 * @author mbpolan
 */
public class LoginResponse extends AbstractResponse {

    private final Integer id;
    private String result;

    public static LoginResponse createSuccess(int id) {
        return new LoginResponse(id, LoginResult.SUCCESS.getValue());
    }

    public static LoginResponse createFailure(LoginResult result) {
        return new LoginResponse(null, result.getValue());
    }

    public Integer getId() {
        return id;
    }

    public String getResult() {
        return result;
    }

    private LoginResponse(Integer id, String result) {
        super("login");

        this.id = id;
        this.result = result;
    }
}
