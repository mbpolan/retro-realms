package com.mbpolan.retrorealms.services.map;

import com.mbpolan.retrorealms.services.beans.Rectangle;

/**
 * Descriptor for a door that takes players to other parts of the map.
 *
 * @author mbpolan
 */
public class Door {

    private int id;
    private int connectsToId;
    private Rectangle region;

    public Door(int id, int connectsToId, Rectangle region) {
        this.id = id;
        this.connectsToId = connectsToId;
        this.region = region;
    }

    public int getId() {
        return id;
    }

    public int getConnectsToId() {
        return connectsToId;
    }

    public Rectangle getRegion() {
        return region;
    }
}
