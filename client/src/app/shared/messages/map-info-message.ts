/**
 * Message that contains a description of a map area.
 *
 * The map area includes tiles and sprites. Tiles are identified by an ID number, along with geometric
 * dimensions. Sprites are reported with all information the client needs to render them on-screen and
 * later identify them.
 */
export class MapInfoMessage {
    width: number;
    height: number;
    tiles: Array<number>;
    players: Array<MapPlayerInfo>
}

/**
 * Data about a single player on the map.
 */
export class MapPlayerInfo {
    id: number;
    username: string;
    sprite: string;
    x: number;
    y: number;
    dir: string;
}
