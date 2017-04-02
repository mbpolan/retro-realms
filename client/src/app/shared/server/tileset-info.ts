import {TileInfo} from "./tile-info";

/**
 * Class that provides information about the tileset that is provided by the game server.
 */
export class TilesetInfo {

    name: string;
    resource: string;
    tiles: Array<TileInfo>;
}