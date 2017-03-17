package com.mbpolan.retrorealms.beans.requests;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author Mike Polan
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "header")
@JsonSubTypes({
        @JsonSubTypes.Type(value = LoginRequest.class, name = "login")
})
public abstract class AbstractRequest {

    protected String header;

    public String getHeader() {
        return header;
    }
}
