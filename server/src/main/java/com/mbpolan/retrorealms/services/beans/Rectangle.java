package com.mbpolan.retrorealms.services.beans;

/**
 * A rectangle defined by Cartesian coordinates.
 *
 * @author mbpolan
 */
public class Rectangle {

    private int x1;
    private int y1;
    private int x2;
    private int y2;

    public Rectangle() {
    }

    public Rectangle(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public Rectangle copy() {
        return new Rectangle(x1, y1, x2, y2);
    }

    public int getX1() {
        return x1;
    }

    public void setX1(int x1) {
        this.x1 = x1;
    }

    public int getY1() {
        return y1;
    }

    public void setY1(int y1) {
        this.y1 = y1;
    }

    public int getX2() {
        return x2;
    }

    public void setX2(int x2) {
        this.x2 = x2;
    }

    public int getY2() {
        return y2;
    }

    public void setY2(int y2) {
        this.y2 = y2;
    }

    public void translate(int dx, int dy) {
        this.x1 += dx;
        this.x2 += dx;
        this.y1 += dy;
        this.y2 += dy;
    }

    public boolean overlaps(Rectangle that) {
        return this.x1 < that.x2 && this.x2 > that.x1 && this.y1 < that.y2 && this.y2 > that.y1;
    }
}