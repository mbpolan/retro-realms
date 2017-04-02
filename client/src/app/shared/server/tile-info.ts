import {FrameInfo} from "./frame-info";

/**
 * Class that describes the characteristics of a tile.
 */
export class TileInfo {

    id: number;
    frame: FrameInfo;
    bbox: Array<FrameInfo>;
}