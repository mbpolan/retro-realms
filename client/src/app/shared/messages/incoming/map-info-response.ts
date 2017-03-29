import {MapPlayerInfo} from "./map-player-info";
/**
 * Message that contains a description of a map area.
 *
 * The map area includes tiles and sprites. Tiles are identified by an ID number, along with geometric
 * dimensions. Sprites are reported with all information the client needs to render them on-screen and
 * later identify them.
 */
export class MapInfoResponse {

    width: number;
    height: number;
    layers: Array<Array<number>>;
    players: Array<MapPlayerInfo>
}
