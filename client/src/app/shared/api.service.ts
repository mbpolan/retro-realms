import { Injectable } from '@angular/core';
import {SocketService, SocketState} from "./socket.service";
import {Subject} from "rxjs";
import {Message, MessageHeader} from "./message";
import {ISubscription} from "rxjs/Subscription";
import {GameEvent, GameEventType} from "./game-event";
import {LoginMessage} from "./messages";

@Injectable()
export class ApiService {

  private events: Subject<GameEvent>;
  private socket: Subject<Message>;
  private username: string;
  private password: string;

  public constructor(private socketService: SocketService) {
    this.events = new Subject<GameEvent>();
    this.socketService.subscribe(this.onStateChange.bind(this));
  }

  public subscribe(func: (e: GameEvent) => void): ISubscription {
    return this.events.subscribe(func);
  }

  public login(username: string, password: string): void {
    this.socket = this.socketService.connect();
    this.username = username;
    this.password = password;

    this.socket.subscribe(this.processMessage.bind(this));
  }

  public logout(): void {
    this.socketService.disconnect();
  }

  private onStateChange(state: SocketState): void {
    if (state == SocketState.CONNECTED) {
      console.log('connected, sending credentials');

      this.socket.next(new Message(MessageHeader.LOGIN, {
        username: this.username,
        password: this.password
      }));
    }

    else if (state == SocketState.DISCONNECTED) {
      console.log('disconnected from server');
      this.events.next(new GameEvent(GameEventType.LOGGED_OUT));
    }
  }

  private processMessage(frame: any): void {
    let message = JSON.parse(frame.body);

    // check the header
    let header = message.header;
    switch (header) {
      case MessageHeader.LOGIN:
        this.processLogin(<LoginMessage> message);
        break;

      default:
        console.error(`Unknown header: ${header}`);
        break;
    }
  }

  private processLogin(message: LoginMessage): void {
    console.log(`login result: ${message.success}`);

    if (message.success) {
      this.events.next(new GameEvent(GameEventType.LOGGED_IN));
    }

    else {
      this.socketService.disconnect();
    }
  }
}
