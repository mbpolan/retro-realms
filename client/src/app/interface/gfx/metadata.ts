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
    bbox: BoxInfo;
    animations: Array<AnimInfo>;
}

/**
 * Class that describes a spritesheet.
 */
export class SpriteSheetInfo {

    name: string;
    sprites: Array<SpriteInfo>;
}
