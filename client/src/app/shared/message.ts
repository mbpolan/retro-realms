export class MessageHeader {

  public static get CONNECTED():string { return "connected"; }
  public static get LOGIN():string { return "login"; }
}

export class Message {
  header: string;
  data: Object;

  public constructor(header: string, data: Object = null) {
    this.header = header;
    this.data = data;
  }
}
