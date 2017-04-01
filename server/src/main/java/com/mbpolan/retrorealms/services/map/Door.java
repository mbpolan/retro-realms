package com.mbpolan.retrorealms.services.map;

import com.mbpolan.retrorealms.services.beans.Rectangle;

/**
 * Descriptor for a door that takes players to other parts of the map.
 *
 * @author mbpolan
 */
public class Door {

    private int id;
    private int srcAreaId;
    private int toAreaId;
    private int toX;
    private int toY;
    private Rectangle bounds;

    /**
     * Creates a new door descriptor.
     *
     * @param id The ID number of this door.
     * @param srcAreaId The ID number of the map area the door is in.
     * @param toAreaId The ID number of the map area the door leads to.
     * @param toX The relative x coordinate in the destination map area, in pixels.
     * @param toY The relative y coordinate in the destination map area, in pixels.
     * @param bounds The collision plane spanned by this door, in pixels.
     */
    public Door(int id, int srcAreaId, int toAreaId, int toX, int toY, Rectangle bounds) {
        this.id = id;
        this.srcAreaId = srcAreaId;
        this.toAreaId = toAreaId;
        this.toX = toX;
        this.toY = toY;
        this.bounds = bounds;
    }

    /**
     * Returns the ID number of this door.
     *
     * @return The ID number.
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the ID of the map area where this door is located in.
     *
     * @return The source map area ID number.
     */
    public int getSrcAreaId() {
        return srcAreaId;
    }

    /**
     * Returns the ID of the map area where this door leads to.
     *
     * @return The destination map area ID number.
     */
    public int getToAreaId() {
        return toAreaId;
    }

    /**
     * Returns the relative x coordinate in the destination map area where this door leads to.
     *
     * @return The relative x coordinate, in pixels.
     */
    public int getToX() {
        return toX;
    }

    /**
     * Returns the relative y coordinate in the destination map area where this door leads to.
     *
     * @return The relative y coordinate, in pixels.
     */
    public int getToY() {
        return toY;
    }

    /**
     * Returns the collision plane spanned by this door.
     *
     * The rectangle is in pixel coordinates, relative to the origin of the map area where it resides.
     *
     * @return The collision plane of the door, in pixels.
     */
    public Rectangle getBounds() {
        return bounds;
    }
}
