package com.mbpolan.retrorealms.services.map;

import com.mbpolan.retrorealms.services.beans.Rectangle;

/**
 * Descriptor for a single area of the map.
 *
 * @author mbpolan
 */
public class Area {

    private int id;
    private Rectangle bounds;

    public Area(int id, Rectangle bounds) {
        this.id = id;
        this.bounds = bounds;
    }

    public int getId() {
        return id;
    }

    public Rectangle getBounds() {
        return bounds;
    }
}
