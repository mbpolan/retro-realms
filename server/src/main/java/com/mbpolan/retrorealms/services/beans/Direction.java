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

    public static Direction fromValue(String value) {
        for (Direction dir : values()) {
            if (dir.value.equals(value)) {
                return dir;
            }
        }

        throw new IllegalArgumentException(String.format("Unknown direction: %s", value));
    }

    public String getValue() {
        return value;
    }

    String value;
    Direction(String value) {
        this.value = value;
    }
}
