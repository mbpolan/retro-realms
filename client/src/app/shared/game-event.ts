// TODO: this file needs to be split into individual ones

export enum GameEventType {
    LOGGED_IN,
    LOGGED_OUT,
    MAP_INFO,
    GAME_STATE,
    MOVE_START,
    MOVE_STOP,
    ENTITY_APPEAR,
    ENTITY_DISAPPEAR
}

export enum LoginResult {
    SUCCESS,
    INVALID_LOGIN,
    SERVER_ERROR
}

export abstract class GameEvent {

    event: GameEventType;

    public constructor(event: GameEventType) {
        this.event = event;
    }
}

export class LoginEvent extends GameEvent {

    id: number;
    result: LoginResult;

    public constructor(id: number, result: LoginResult) {
        super(GameEventType.LOGGED_IN);

        this.id = id;
        this.result = result;
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
    layers: Array<Array<number>>;
    players: Array<PlayerInfo>;

    public constructor(width: number, height: number, layers: Array<Array<number>>, players: Array<PlayerInfo>) {
        super(GameEventType.MAP_INFO);

        this.width = width;
        this.height = height;
        this.layers = layers;
        this.players = players;
    }
}

export class GameStateEvent extends GameEvent {

    players: Array<PlayerInfo>;

    public constructor(players: Array<PlayerInfo>) {
        super(GameEventType.GAME_STATE);

        this.players = players;
    }
}

export class MoveStartEvent extends GameEvent {

    id: number;
    dir: string;

    public constructor(id: number, dir: string) {
        super(GameEventType.MOVE_START);

        this.id = id;
        this.dir = dir;
    }
}

export class MoveStopEvent extends GameEvent {

    id: number;
    x: number;
    y: number;

    public constructor(id: number, x: number, y: number) {
        super(GameEventType.MOVE_STOP);

        this.id = id;
        this.x = x;
        this.y = y;
    }
}

export class EntityAppearEvent extends GameEvent {

    player: PlayerInfo;

    public constructor(player: PlayerInfo) {
        super(GameEventType.ENTITY_APPEAR);

        this.player = player;
    }
}

export class EntityDisappearEvent extends GameEvent {

    id: number;

    public constructor(id: number) {
        super(GameEventType.ENTITY_DISAPPEAR);

        this.id = id;
    }
}