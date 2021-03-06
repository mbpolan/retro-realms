import {Observable, Subject, Observer} from "rxjs/Rx";
import {Injectable} from "@angular/core";
import {ISubscription} from "rxjs/Subscription";
import {Message} from "./messages/message";
import {AppService} from "./app.service";

declare var Stomp: any;
declare var SockJS: any;

export enum SocketState {
  CONNECTED,
  DISCONNECTED
}

@Injectable()
export class SocketService {

  private client: any;
  private state: SocketState;
  private stateSubject: Subject<SocketState>;

  public constructor(private app: AppService) {
    this.state = SocketState.DISCONNECTED;
    this.stateSubject = new Subject<SocketState>();
  }

  public subscribe(func: (state: SocketState) => void): ISubscription {
    return this.stateSubject.subscribe(func);
  }

  public connect(cb: () => void): Subject<Message> {
    console.log('connecting...');
    let self = this;

    let observable = Observable.create((obs: Observer<Message>) => {
      console.log('stomp client...');
      self.client = Stomp.over(new SockJS(`${this.app.contextPath()}/client`));

      // establish a connection to the server and subscribe for game events
      self.client.connect({}, () => {
        console.log('connected!');

        self.client.subscribe(`/user/queue/game`, (msg) => obs.next(msg));

        // notify listeners that we're connected now
        self.stateSubject.next(SocketState.CONNECTED);

        // invoke the connected callback up front
        cb();
      }, (err: any) => {
        console.log(err);

        self.stateSubject.next(SocketState.DISCONNECTED);
      });

      // clean-up by disconnecting if needed
      return () => this.disconnect();
    });

    let observer = {
      next: (message: Message) => {
        self.client.send('/api/game', {}, JSON.stringify(message));
      }
    };

    return Subject.create(observer, observable);
  }

  public disconnect(): void {
    console.log('Disconnecting...');

    if (this.client) {
      this.client.disconnect(() => {
        this.client = null;

        // notify listeners that we've disconnected
        this.stateSubject.next(SocketState.DISCONNECTED);
      });
    }
  }
}
