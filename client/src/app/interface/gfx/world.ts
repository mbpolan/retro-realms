import {Entity} from "./entity";
import {Layer} from "./layer";

/**
 * Canvas that renders the entire game world.
 */
export class World extends PIXI.Container {

    private layers: Array<Layer>;

    public constructor() {
        super();

        this.layers = [];
    }

    /**
     * Clears out all elements of the world.
     */
    public reset(): void {
        this.removeChildren();
        this.layers = [];
    }

    /**
     * Adds one or more layers to the world.
     *
     * @param layers A list of map layers.
     */
    public addLayers(layers: Array<Layer>): void {
        layers.forEach(layer => {
            this.layers.push(layer);
            this.addChild(layer);
        });
    }

    /**
     * Places an entity on the world.
     *
     * @param entity The entity to add.
     */
    public addEntity(entity: Entity): void {
        this.getEntityLayer().addChild(entity);
    }

    /**
     * Returns the layer that all entities should be placed on.
     *
     * @returns {PIXI.Container} The layer for entities.
     */
    private getEntityLayer(): PIXI.Container {
        if (this.layers.length === 0) {
            return null;
        }

        else if (this.layers.length === 1) {
            return this.layers[0];
        }

        else {
            return this.layers[this.layers.length - 2];
        }
    }
}