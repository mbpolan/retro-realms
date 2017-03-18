import {Injectable} from "@angular/core";
import {SocketService, SocketState} from "./socket.service";
import {Subject} from "rxjs";
import {Message, MessageHeader, LoginMessage, MapInfoMessage, GameStateMessage} from "./messages";
import {ISubscription} from "rxjs/Subscription";
import {GameEvent, GameEventType, MapInfoEvent, GameStateEvent} from "./game-event";

@Injectable()
export class ApiService {

  private events: Subject<GameEvent>;
  private socket: Subject<Message>;

  public constructor(private socketService: SocketService) {
    this.events = new Subject<GameEvent>();
    this.socketService.subscribe(this.onStateChange.bind(this));
  }

  /**
   * Subscribe for events from the server.
   *
   * @param func The callback to invoke.
   * @returns {Subscription} Handle for the subscription.
   */
  public subscribe(func: (e: GameEvent) => void): ISubscription {
    return this.events.subscribe(func);
  }

  /**
   * Connects and logs into the server.
   *
   * @param username The player's username.
   * @param password The player's password.
   */
  public login(username: string, password: string): void {
    this.socket = this.socketService.connect(() => {

      // send login information upon connecting
      console.log('sending credentials');
      this.socket.next(new Message(MessageHeader.LOGIN, {
        username: username,
        password: password
      }));
    });

    this.socket.subscribe(this.processMessage.bind(this));
  }

  /**
   * Logs out and disconnects from the server.
   */
  public logout(): void {
    this.socketService.disconnect();
  }

  /**
   * Handler invoked when the state of the websocket connection changes.
   *
   * @param state The new socket state.
   */
  private onStateChange(state: SocketState): void {
    if (state == SocketState.CONNECTED) {
      console.log('connected to server');
    }

    else if (state == SocketState.DISCONNECTED) {
      console.log('disconnected from server');
      this.events.next(new GameEvent(GameEventType.LOGGED_OUT));
    }
  }

  /**
   * Processes an incoming message from the server.
   *
   * @param frame The message frame.
   */
  private processMessage(frame: any): void {
    let message = JSON.parse(frame.body);

    // check the header
    let header = message.header;
    switch (header) {
      case MessageHeader.LOGIN:
        this.processLogin(<LoginMessage> message);
        break;

      case MessageHeader.MAP_INFO:
        this.processMapInfo(<MapInfoMessage> message);
            break;

      case MessageHeader.GAME_STATE:
        this.processGameState(<GameStateMessage> message);
            break;

      default:
        console.error(`Unknown header: ${header}`);
        break;
    }
  }

  /**
   * Processes a login message from the server.
   *
   * @param message The message.
   */
  private processLogin(message: LoginMessage): void {
    console.log(`login result: ${message.success}`);

    if (message.success) {
      this.events.next(new GameEvent(GameEventType.LOGGED_IN));
    }

    else {
      this.socketService.disconnect();
    }
  }

  /**
   * Processes a map information message from the server.
   *
   * @param message The message.
   */
  private processMapInfo(message: MapInfoMessage): void {
    this.events.next(new MapInfoEvent(message.width, message.height, message.tiles));
  }

  /**
   * Processes a game state update message from the server.
   *
   * @param message The message.
   */
  private processGameState(message: GameStateMessage): void {
    this.events.next(new GameStateEvent());
  }
}