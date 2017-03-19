import {Message, MessageHeader} from "../message";

/**
 * Message that contains a request to stop moving the player.
 */
export class MoveStopRequest extends Message {

    public constructor() {
        super(MessageHeader.MOVE_STOP);
    }
}