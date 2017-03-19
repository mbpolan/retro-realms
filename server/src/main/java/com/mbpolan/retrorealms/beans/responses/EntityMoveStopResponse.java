package com.mbpolan.retrorealms.beans.responses;

/**
 * @author Mike Polan
 */
public class EntityMoveStopResponse extends AbstractResponse {

    private int id;

    public EntityMoveStopResponse(int id) {
        super("moveStop");

        this.id = id;
    }

    public int getId() {
        return id;
    }
}
