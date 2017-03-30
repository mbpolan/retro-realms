import {Entity} from "./entity";
import {Layer} from "./layer";
import {Direction} from "../../shared/direction";

/**
 * Canvas that renders the entire game world.
 */
export class World extends PIXI.Container {

    private tileWidth: number;
    private tileHeight: number;
    private layers: Array<Layer>;
    private entities: Map<number, Entity>;
    private _primaryEntity: Entity;

    /**
     * Creates a new world renderer.
     *
     * @param sceneWidth The pixel width of the viewport.
     * @param sceneHeight The pixel height of the viewport.
     * @param tileSize The square size of a single tile, in pixels.
     */
    public constructor(private sceneWidth: number, private sceneHeight: number, private tileSize: number) {
        super();

        this.layers = [];
    }

    /**
     * Clears out all elements of the world.
     *
     * @param tileWidth The new width of the map.
     * @param tileHeight The new height of the map.
     */
    public reset(tileWidth?: number, tileHeight?: number): void {
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;

        this.removeChildren();
        this.layers = [];

        // initialize data structures
        this.entities = new Map<number, Entity>();
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
     * @param id The ID number to associate with the entity.
     * @param entity The entity to add.
     */
    public addEntity(id: number, entity: Entity): void {
        this.entities[id] = entity;

        // add the entity to its proper layer
        this.getEntityLayer().addChild(entity);
    }

    /**
     * Sets the entity that represents the local player.
     *
     * @param entity The {@link Entity}.
     */
    public set primaryEntity(entity: Entity) {
        this._primaryEntity = entity;

        // reposition the world to be centered at this entity
        this.centerAround(entity.x, entity.y);
    }

    /**
     * Removes the entity with the given ID number from the world.
     *
     * @param id The ID number of the entity.
     */
    public removeEntityById(id: number): void {
        let entity = this.entities[id];
        if (entity) {
            this.getEntityLayer().removeChild(entity);
            delete this.entities[id];
        }
    }

    /**
     * Returns the {@link Entity} with the associated ID number.
     *
     * @param id The ID number of the entity.
     * @returns {Entity} The corresponding entity.
     */
    public getEntityById(id: number): Entity {
        return this.entities[id];
    }

    /**
     * Updates the current state of the world.
     *
     * This will cause any moving entities to progress to their next frame of motion.
     */
    public update(): void {
        let now = Date.now();

        for (let id in this.entities) {
            if (this.entities.hasOwnProperty(id)) {
                let entity = this.entities[id];

                if (entity.moving) {
                    // only animate the entity if this is not their very first frame
                    if (entity.lastFrame > 0) {
                        // compute how long its been since we last rendered this entity
                        let deltaT = now - entity.lastFrame;

                        // we should move S units every D ms, where S is the entity's speed and D is the walk delay
                        // so we need to move ((now - last) / D) * S units on each frame of motion
                        // FIXME: get this info from the server
                        // TODO: compensate for lag
                        let span = (deltaT / 100) * 8;

                        // compute the velocity vector based on the entity's direction of motion
                        let dx = 0, dy = 0;
                        switch (entity.direction) {
                            case Direction.UP:
                                dy = -1;
                                break;
                            case Direction.DOWN:
                                dy = 1;
                                break;
                            case Direction.LEFT:
                                dx = -1;
                                break;
                            case Direction.RIGHT:
                                dx = 1;
                                break;
                        }

                        // reposition the entity
                        entity.x += span * dx;
                        entity.y += span * dy;

                        // if the moving entity represents the local player, center the world around them
                        if (this._primaryEntity == entity) {
                            this.centerAround(entity.x, entity.y);
                        }
                    }

                    // mark this as the last frame where the entity was rendered
                    entity.lastFrame = now;
                }
            }
        }
    }

    /**
     * Centers the world around a given point.
     *
     * @param x The x coordinate of the point, in pixels.
     * @param y The y coordinate of the point, in pixels.
     */
    private centerAround(x: number, y: number): void {
        let mapHeight = this.tileHeight * this.tileSize;
        let mapWidth = this.tileWidth * this.tileSize;

        // compute the distance from the center of the world on the y axis
        let dt = y - (this.sceneHeight / 2);
        let y1 = dt < 0 ? 0 : Math.min(mapHeight - this.sceneHeight, dt) * -1;

        // compute the distance from the center of the world on the x axis
        let du = x - (this.sceneWidth / 2);
        let x1 = du < 0 ? 0 : Math.min(mapWidth - this.sceneWidth, du) * -1;

        this.position.set(x1, y1);
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