package com.mbpolan.retrorealms.services.beans;

import java.util.List;

/**
 * @author mbpolan
 */
public class Tile {

    private int id;
    private Rectangle boundingBox;

    public Tile(int id, Rectangle boundingBox) {
        this.id = id;
        this.boundingBox = boundingBox;
    }

    public int getId() {
        return id;
    }

    public boolean hasBoundingBox() {
        return boundingBox != null;
    }

    public Rectangle getBoundingBox() {
        return boundingBox;
    }
}
