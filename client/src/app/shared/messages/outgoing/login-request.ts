import {Message, MessageHeader} from "../message";

/**
 * Message that contains a request to login to the game.
 */
export class LoginRequest extends Message {

    private username: string;
    private password: string;

    public constructor(username: string, password: string) {
        super(MessageHeader.LOGIN);

        this.username = username;
        this.password = password;
    }
}