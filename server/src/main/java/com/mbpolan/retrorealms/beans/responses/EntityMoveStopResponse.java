package com.mbpolan.retrorealms.beans.responses;

/**
 * @author Mike Polan
 */
public class EntityMoveStopResponse extends AbstractResponse {

    private int id;
    private int x;
    private int y;

    public EntityMoveStopResponse(int id, int x, int y) {
        super("moveStop");

        this.id = id;
        this.x = x;
        this.y = y;
    }

    public int getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
