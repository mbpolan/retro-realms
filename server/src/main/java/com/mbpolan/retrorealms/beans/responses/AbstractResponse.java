package com.mbpolan.retrorealms.beans.responses;

/**
 * @author mbpolan
 */
public abstract class AbstractResponse {

    protected String header;

    public AbstractResponse(String header) {
        this.header = header;
    }

    public String getHeader() {
        return header;
    }
}
