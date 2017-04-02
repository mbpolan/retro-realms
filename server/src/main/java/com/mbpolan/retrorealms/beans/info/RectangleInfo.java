package com.mbpolan.retrorealms.beans.info;

/**
 * Bean that describes a rectangle in terms of width and height.
 *
 * @author mbpolan
 */
public class RectangleInfo {

    private int x;
    private int y;
    private int w;
    private int h;

    public RectangleInfo(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

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
