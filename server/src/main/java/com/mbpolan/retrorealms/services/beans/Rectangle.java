package com.mbpolan.retrorealms.services.beans;

/**
 * A rectangle defined by two 2D points.
 *
 * Rectangles span the area enclosed by a top-left coordinate (x1,y1) to a bottom-right coordinate (x2,y2).
 *
 * @author mbpolan
 */
public class Rectangle {

    private int x1;
    private int y1;
    private int x2;
    private int y2;

    /**
     * Creates a rectangle of no width or height, located at the origin.
     */
    public Rectangle() {
    }

    /**
     * Creates a rectangle defined by coordinates.
     *
     * @param x1 The left coordinate.
     * @param y1 The top coordinate.
     * @param x2 The right coordinate.
     * @param y2 The bottom coordinate.
     */
    public Rectangle(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    /**
     * Returns the width of the rectangle.
     *
     * @return The width.
     */
    public int getWidth() {
        return this.x2 - this.x1;
    }

    /**
     * Returns the height of the rectangle.
     *
     * @return The height.
     */
    public int getHeight() {
        return this.y2 - this.y1;
    }

    /**
     * Returns the left coordinate.
     *
     * @return The x1 coordinate.
     */
    public int getX1() {
        return x1;
    }

    /**
     * Sets the left coordinate.
     *
     * @param x1 The left coordinate.
     */
    public void setX1(int x1) {
        this.x1 = x1;
    }

    /**
     * Returns the top coordinate.
     *
     * @return The y1 coordinate.
     */
    public int getY1() {
        return y1;
    }

    /**
     * Sets the top coordinate.
     *
     * @param y1 The top coordinate.
     */
    public void setY1(int y1) {
        this.y1 = y1;
    }

    /**
     * Returns the right coordinate.
     *
     * @return The x2 coordinate.
     */
    public int getX2() {
        return x2;
    }

    /**
     * Sets the right coordinate.
     *
     * @param x2 The right coordinate.
     */
    public void setX2(int x2) {
        this.x2 = x2;
    }

    /**
     * Returns the bottom coordinate.
     *
     * @return The y2 coordinate.
     */
    public int getY2() {
        return y2;
    }

    /**
     * Sets the bottom coordinate.
     *
     * @param y2 The bottom coordinate.
     */
    public void setY2(int y2) {
        this.y2 = y2;
    }

    /**
     * Returns a copy of this rectangle.
     *
     * @return A cloned rectangle.
     */
    public Rectangle copy() {
        return new Rectangle(x1, y1, x2, y2);
    }

    public Rectangle relativeTo(Rectangle other) {
        int rx = x1 - other.x1;
        int ry = y1 - other.y1;

        return new Rectangle(rx, ry, rx + (x2 - x1), ry + (y2 - y1));
    }

    /**
     * Tests if a point lies within the rectangle.
     *
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @return true if the point is enclosed by the rectangle, false if not.
     */
    public boolean contains(int x, int y) {
        return ((x >= x1 && x <= x2) && (y >= y1 && y <= y2));
    }

    /**
     * Tests if another rectangle is completely contained within this rectangle.
     *
     * @param that The rectangle.
     * @return true if this this rectangle contains the other, false if not.
     */
    public boolean contains(Rectangle that) {
        return (that.x1 >= this.x1 && that.x2 <= this.x2) && (that.y1 >= this.y1 && that.y2 <= this.y2);
    }

    /**
     * Tests if this rectangle overlaps another either completely or partially.
     *
     * @param that The rectangle.
     * @return true if the rectangles overlap, false if not.
     */
    public boolean overlaps(Rectangle that) {
        return this.x1 < that.x2 && this.x2 > that.x1 && this.y1 < that.y2 && this.y2 > that.y1;
    }

    /**
     * Multiples the coordinates of this rectangle by some scalar.
     *
     * @param x The scalar to multiply x coordinates by.
     * @param y The scalar to multiple y coordinates by.
     * @return A new rectangle.
     */
    public Rectangle multiply(int x, int y) {
        return new Rectangle(this.x1 * x, this.y1 * y, this.x2 * x, this.y2 * y);
    }

    /**
     * Translates the rectangle by some scalar.
     *
     * @param dx The scalar to translate x coordinates by.
     * @param dy The scalar to translate y coordinates by.
     */
    public void translate(int dx, int dy) {
        this.x1 += dx;
        this.x2 += dx;
        this.y1 += dy;
        this.y2 += dy;
    }
}
