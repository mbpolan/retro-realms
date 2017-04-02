package com.mbpolan.retrorealms.beans.info;

import java.util.List;

/**
 * Bean that provides information about a single tile.
 *
 * @author mbpolan
 */
public class TileMetadataInfo {

    private int id;
    private RectangleInfo frame;
    private List<RectangleInfo> bbox;

    public TileMetadataInfo(int id, RectangleInfo frame, List<RectangleInfo> bbox) {
        this.id = id;
        this.frame = frame;
        this.bbox = bbox;
    }

    public int getId() {
        return id;
    }

    public RectangleInfo getFrame() {
        return frame;
    }

    public List<RectangleInfo> getBbox() {
        return bbox;
    }
}
