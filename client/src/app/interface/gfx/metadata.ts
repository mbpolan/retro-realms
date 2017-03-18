/**
 * Class that describes a rectangle.
 */
export class BoxInfo {

    x: number;
    y: number;
    w: number;
    h: number;
}

/**
 * Class that describes an animation sequence.
 */
export class AnimInfo {

    name: string;
    frames: Array<BoxInfo>;
}

/**
 * Class that describes a sprite.
 */
export class SpriteInfo {

    name: string;
    animations: Array<AnimInfo>;
}

/**
 * Class that describes a spritesheet.
 */
export class SpriteSheetInfo {

    name: string;
    sprites: Array<SpriteInfo>;
}

/**
 * Class that describes a tile.
 */
export class TileInfo {

    id: number;
    box: BoxInfo;
}

/**
 * Class that describes a tileset.
 */
export class TilesetInfo {

    name: string;
    tiles: Array<TileInfo>;
}