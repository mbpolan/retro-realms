import {MapPlayerInfo} from "./map-player-info";

/**
 * Message that describes the current state of the game.
 *
 * The game state contains information about elements of the map that may or have changed.
 */
export class GameStateResponse {

    players: Array<MapPlayerInfo>;
}