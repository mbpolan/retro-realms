package com.mbpolan.retrorealms.beans.requests;

/**
 * @author Mike Polan
 */
public enum RequestHeader {

    LOGIN (1);

    int code;
    RequestHeader(int code) {
        this.code = code;
    }
}
