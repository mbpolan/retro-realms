export class MessageHeader {

  public static get CONNECTED():string { return "connected"; }
  public static get LOGIN():string { return "login"; }
  public static get MAP_INFO():string { return "mapInfo"; }
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