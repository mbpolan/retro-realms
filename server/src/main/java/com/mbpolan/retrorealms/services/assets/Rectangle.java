package com.mbpolan.retrorealms.services.assets;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Descriptor for a frame or box defined by an asset.
 *
 * @author mbpolan
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Rectangle {

    private int x;
    private int y;
    private int w;
    private int h;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }
}
