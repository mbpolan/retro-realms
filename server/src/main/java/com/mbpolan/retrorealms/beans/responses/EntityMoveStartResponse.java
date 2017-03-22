package com.mbpolan.retrorealms.beans.responses;

/**
 * @author mbpolan
 */
public class EntityMoveStartResponse extends AbstractResponse {

    private int id;
    private String dir;

    public EntityMoveStartResponse(int id, String dir) {
        super("moveStart");

        this.id = id;
        this.dir = dir;
    }

    public int getId() {
        return id;
    }

    public String getDir() {
        return dir;
    }
}
