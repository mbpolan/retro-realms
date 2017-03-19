export enum GameEventType {
  LOGGED_IN,
  LOGGED_OUT,
  MAP_INFO,
  GAME_STATE
}

export abstract class GameEvent {

  event: GameEventType;

  public constructor(event: GameEventType) {
    this.event = event;
  }
}

export class LoginEvent extends GameEvent {

  id: number;
  success: boolean;

  public constructor(id: number, success: boolean) {
    super(GameEventType.LOGGED_IN);

    this.id = id;
    this.success = success;
  }
}

export class LogoutEvent extends GameEvent {

  public constructor() {
    super(GameEventType.LOGGED_OUT);
  }
}

export class PlayerInfo {
  id: number;
  username: string;
  sprite: string;
  x: number;
  y: number;
  dir: string;
}

export class MapInfoEvent extends GameEvent {

  width: number;
  height: number;
  tiles: Array<number>;
  players: Array<PlayerInfo>;

  public constructor(width: number, height: number, tiles: Array<number>, players: Array<PlayerInfo>) {
    super(GameEventType.MAP_INFO);

    this.width = width;
    this.height = height;
    this.tiles = tiles;
    this.players = players;
  }
}

export class GameStateEvent extends GameEvent {

  constructor() {
    super(GameEventType.GAME_STATE);
  }
}