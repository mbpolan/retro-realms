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
  id: number;
  success: boolean;
}

export class PlayerInfoMessage {
  id: number;
  username: string;
  sprite: string;
  x: number;
  y: number;
}

export class MapInfoMessage {
  width: number;
  height: number;
  tiles: Array<number>;
  players: Array<PlayerInfoMessage>
}

export class GameStateMessage {
}