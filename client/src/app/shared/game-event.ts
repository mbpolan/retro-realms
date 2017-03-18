export enum GameEventType {
  LOGGED_IN,
  LOGGED_OUT,
  MAP_INFO
}

export class GameEvent {

  event: GameEventType;

  public constructor(event: GameEventType) {
    this.event = event;
  }
}

export class MapInfoEvent extends GameEvent {

  width: number;
  height: number;
  tiles: Array<number>;

  public constructor(width: number, height: number, tiles: Array<number>) {
    super(GameEventType.MAP_INFO);

    this.width = width;
    this.height = height;
    this.tiles = tiles;
  }
}