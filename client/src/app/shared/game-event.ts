export enum GameEventType {
  LOGGED_IN,
  LOGGED_OUT
}

export class GameEvent {

  event: GameEventType;

  public constructor(event: GameEventType) {
    this.event = event;
  }
}
