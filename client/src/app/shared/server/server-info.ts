import {SpritesInfo} from "./sprites-info";
import {TilesetInfo} from "./tileset-info";

/**
 * Class that contains the response from a server information request.
 */
export class ServerInfo {

    tileSize: number;
    tileset: TilesetInfo;
    sprites: SpritesInfo;
}