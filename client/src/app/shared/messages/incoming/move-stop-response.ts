/**
 * Message that contains information about an entity that has stopped moving.
 *
 * A stoppage in movement applies to an entity, identified by their ID number. The entity's final position
 * is also communicated.
 */
export class MoveStopResponse {

    id: number;
    x: number;
    y: number;
}