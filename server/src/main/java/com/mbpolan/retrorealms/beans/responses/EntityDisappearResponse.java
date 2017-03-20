package com.mbpolan.retrorealms.beans.responses;

/**
 * @author Mike Polan
 */
public class EntityDisappearResponse extends AbstractResponse {

    private int id;

    public EntityDisappearResponse(int id) {
        super("entityDisappear");

        this.id = id;
    }

    public int getId() {
        return id;
    }
}
