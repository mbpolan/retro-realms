package com.mbpolan.retrorealms.services.beans;

import com.mbpolan.retrorealms.services.map.Door;

/**
 * Describes the result of a player movement on a map area.
 *
 * @author mbpolan
 */
public class MoveAction {

    private Action action;
    private Door door;

    /**
     * Enumeration of possible movement actions.
     */
    public enum Action {
        MOVED,
        RELOCATE_TO_DOOR,
        COLLISION
    }

    /**
     * Creates a result indicating the player was moved.
     *
     * @return A movement action.
     */
    public static MoveAction move() {
        return new MoveAction(Action.MOVED, null);
    }

    /**
     * Creates a result indicating the player reached a door.
     *
     * @return A movement action.
     */
    public static MoveAction moveToDoor(Door door) {
        return new MoveAction(Action.RELOCATE_TO_DOOR, door);
    }

    /**
     * Creates a result indicating the player collided with another object.
     *
     * @return A movement action.
     */
    public static MoveAction collision() {
        return new MoveAction(Action.COLLISION, null);
    }

    /**
     * Determines if the movement was successful, meaning the player did not collide with anything.
     *
     * @return true if there was no collision, false otherwise.
     */
    public boolean isValid() {
        return action != Action.COLLISION;
    }

    /**
     * Returns the action associated with the movement.
     *
     * @return The action.
     */
    public Action getAction() {
        return action;
    }

    /**
     * Returns the door descriptor if the movement action involves triggering a door.
     *
     * @return A {@link Door} descriptor, or null if not applicable.
     */
    public Door getDoor() {
        return door;
    }

    /**
     * Private constructor.
     *
     * @param action The action contained in this result.
     * @param door The associated door.
     */
    private MoveAction(Action action, Door door) {
        this.action = action;
        this.door = door;
    }
}
