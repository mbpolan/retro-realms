export enum Level {
    GROUND,
    OVERLAY
}

export class World extends PIXI.Container {

    private ground: PIXI.Container;
    private overlay: PIXI.Container;

    public constructor() {
        this.addChild(this.ground);
        this.addChild(this.overlay);
    }
}