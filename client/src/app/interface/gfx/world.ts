export enum Level {
    BASE
}

export class World extends PIXI.Container {

    private ground: PIXI.Container;

    public constructor() {
        super();

        this.addChild(this.ground);
    }
}