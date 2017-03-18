export class MessageHeader {

  public static get CONNECTED():string { return "connected"; }
  public static get LOGIN():string { return "login"; }
  public static get MAP_INFO():string { return "mapInfo"; }
  public static get GAME_STATE():string { return "gameState"; }
}

export class Message {
  header: string;
  data: Object;

  public constructor(header: string, data: Object = null) {
    this.header = header;
    this.data = data;
  }
}

export class LoginMessage {
  success: boolean;
}

export class MapInfoMessage {
  width: number;
  height: number;
  tiles: Array<number>;
}

export class GameStateMessage {
}