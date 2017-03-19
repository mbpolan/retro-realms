import {Message, MessageHeader} from "../message";

/**
 * Message that contains a request to start moving the player.
 */
export class MoveStartRequest extends Message {

    private dir: string;

    public constructor(dir: string) {
        super(MessageHeader.MOVE_START);

        this.dir = dir;
    }
}