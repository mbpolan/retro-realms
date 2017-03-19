package com.mbpolan.retrorealms.beans.requests;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author Mike Polan
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "header", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = LoginRequest.class, name = RequestHeader.LOGIN),
        @JsonSubTypes.Type(value = MoveStartRequest.class, name = RequestHeader.MOVE_START),
        @JsonSubTypes.Type(value = MoveStopRequest.class, name = RequestHeader.MOVE_STOP)
})
public abstract class AbstractRequest {

    protected String header;

    public String getHeader() {
        return header;
    }
}
