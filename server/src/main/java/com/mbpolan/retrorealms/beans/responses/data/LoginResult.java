package com.mbpolan.retrorealms.beans.responses.data;

/**
 * Enumeration of possible login results.
 *
 * @author mbpolan
 */
public enum LoginResult {
    SUCCESS        ("success"),
    INVALID_LOGIN  ("invalidLogin"),
    SERVER_ERROR   ("serverError");

    public String getValue() {
        return value;
    }

    String value;
    LoginResult(String value) {
        this.value = value;
    }
}
