package com.mbpolan.retrorealms.services.assets;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Assets that describes a single tile.
 *
 * @author mbpolan
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TileAsset {

    private Integer id;
    private BoundingBox frame;

    public Integer getId() {
        return id;
    }

    public BoundingBox getFrame() {
        return frame;
    }
}
