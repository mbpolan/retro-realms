package com.mbpolan.retrorealms.services.map;

import com.mbpolan.retrorealms.services.beans.Rectangle;

/**
 * Descriptor for a single tile on the map.
 *
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
