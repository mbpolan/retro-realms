/**
 * Container that renders a single layer of map tiles.
 */
export class Layer extends PIXI.Container {

    public constructor(tiles: Array<PIXI.Container>) {
        super();

        tiles.forEach(t => this.addChild(t));
    }
}