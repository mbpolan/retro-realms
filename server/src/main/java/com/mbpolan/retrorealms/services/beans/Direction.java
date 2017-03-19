package com.mbpolan.retrorealms.services.beans;

/**
 * Enumeration of possible directions.
 *
 * @author Mike Polan
 */
public enum Direction {
    UP    ("up"),
    DOWN  ("down"),
    LEFT  ("left"),
    RIGHT ("right");

    public String getValue() {
        return value;
    }

    String value;
    Direction(String value) {
        this.value = value;
    }
}
