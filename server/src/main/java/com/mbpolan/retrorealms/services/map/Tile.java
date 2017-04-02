package com.mbpolan.retrorealms.services.map;

import com.mbpolan.retrorealms.services.beans.Rectangle;

import java.util.ArrayList;
import java.util.List;

/**
 * Descriptor for a single tile on the map.
 *
 * @author mbpolan
 */
public class Tile {

    private int id;
    private Rectangle frame;
    private List<Rectangle> boundingBoxes;

    public Tile(int id, Rectangle frame) {
        this(id, frame, new ArrayList<>());
    }

    public Tile(int id, Rectangle frame, List<Rectangle> boundingBoxes) {
        this.id = id;
        this.frame = frame;
        this.boundingBoxes = boundingBoxes;
    }

    public int getId() {
        return id;
    }

    public Rectangle getFrame() {
        return frame;
    }

    public boolean hasBoundingBoxes() {
        return !boundingBoxes.isEmpty();
    }

    public List<Rectangle> getBoundingBoxes() {
        return boundingBoxes;
    }
}
