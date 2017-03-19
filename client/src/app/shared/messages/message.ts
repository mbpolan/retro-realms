/**
 * Enumeration of all possible message headers that the server can send.
 */
export class MessageHeader {

    public static get CONNECTED():string { return "connected"; }
    public static get LOGIN():string { return "login"; }
    public static get MAP_INFO():string { return "mapInfo"; }
    public static get GAME_STATE():string { return "gameState"; }
    public static get MOVE_START():string { return "moveStart"; }
    public static get MOVE_STOP():string { return "moveStop"; }
}

/**
 * Generic message sent by the server or the client.
 */
export abstract class Message {

    header: string;

    public constructor(header: string) {
        this.header = header;
    }
}
