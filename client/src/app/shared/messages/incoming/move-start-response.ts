/**
 * Message that contains information about an entity that has started moving.
 *
 * Movement is described by the ID of the entity that's now moving, and the direction that it's
 * moving in. Motion is done only in one direction at a time.
 */
export class MoveStartResponse {

    id: number;
    dir: string;
}