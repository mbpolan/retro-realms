'use strict';

var module = angular.module('wsApp.graphics.world', []);

/**
 * Service that primarily manages and coordinates graphics on the scene.
 */
module.factory('World', ['Creature', 'Global', function (Creature, Global) {

    /**
     * Creates a new viewable "world" view of the game map.
     *
     * @param parent The parent canvas element.
     * @param width The width of the map, in tiles.
     * @param height The height of the map, in tiles.
     * @constructor Creates a new map canvas.
     */
    function World(parent, width, height) {
        PIXI.Container.call(this);

        this.refs = {};
        this.tileSpan = Global.TileSize * Global.TileScale;
        this.map = new PIXI.Container();
        this.entities = new PIXI.Container();
        this.addChild(this.map);
        this.addChild(this.entities);

        this.scene = {w: width, h: height};
        parent.addChild(this);
    }

    World.prototype = Object.create(PIXI.Container.prototype);
    World.prototype.constructor = World;

    /**
     * Initializes a map area for the viewable scene.
     *
     * The world parameter should contain the following properties:
     *   - map {Array} An array containing sprite IDs for the tiles on the map.
     *   - entities {Array} An array containing entity descriptions on the map.
     *
     * @param world {object} Description of the current map area.
     */
    World.prototype.define = function (world) {
        this.map.removeChildren();
        this.entities.removeChildren();

        for (var x = 0; x < this.scene.w; x++) {
            for (var y = 0; y < this.scene.h; y++) {
                var id = world.map[y * this.scene.w + x];

                this.placeTile('tile-' + id, x * this.tileSpan, y * this.tileSpan);
            }
        }

        for (var i = 0; i < world.entities.length; i++) {
            this.addEntity(world.entities[i]);
        }
    };

    /**
     * Processes the world's state on each game loop iteration.
     */
    World.prototype.tick = function () {
        // tick each of the creatures on the map
        for (var key in this.refs) {
            if (this.refs.hasOwnProperty(key)) {
                this.refs[key].tick();
            }
        }
    };

    /**
     * Returns a creature on the map with the given internal ID.
     *
     * @param ref The internal ID of the creature.
     * @returns {Creature} The creature object, or null if not found.
     */
    World.prototype.creatureBy = function (ref) {
        return this.refs[ref];
    };

    /**
     * Adds a new entity to the world.
     *
     * Entities may be either creatures or static objects.
     *
     * @param entity {Entity} The new entity object to add.
     */
    World.prototype.addEntity = function (entity) {
        if (angular.isNumber(entity.ref)) {
            var creature = Creature.cardinal('char', 4)
                .setName(entity.name)
                .setDirection(entity.dir)
                .moveTo(entity.x * 8, entity.y * 8);
            this.entities.addChild(creature.getRoot());

            this.refs[entity.ref] = creature;
        }

        else if (!angular.isNumber(entity.ref)) {
            this.placeEntity('tile-' + entity.id, entity.x * this.tileSpan / 4, entity.y * this.tileSpan / 4);
        }
    };

    /**
     * Removes an existing entity on the map.
     *
     * @param ref {number} The internal ID of the entity to remove.
     */
    World.prototype.removeEntityByRef = function (ref) {
        console.log('Remove: ' + ref);
        var entity = this.refs[ref];
        if (entity) {
            this.entities.removeChild(entity.getRoot());
            this.refs[ref] = undefined;
        }

        else {
            console.log('Unknown entity ref: ' + ref);
        }
    };

    /**
     * Moves an entity from its current position on the map to a new position.
     *
     * This function does not animate the entity as it's moving.
     *
     * @param ref {number} The internal ID of the entity to move.
     * @param x {number} The new x coordinate.
     * @param y {number} The new y coordinate.
     */
    World.prototype.moveEntity = function (ref, x, y) {
        var entity = this.creatureBy(ref);
        if (entity) {
            entity.moveTo(x, y);
        }
    };

    /**
     * Flags that an entity is either now in motion or no longer in motion.
     *
     * @param ref {number} The internal ID of the entity.
     * @param moving {boolean} true if the entity is now moving, false if stopped.
     */
    World.prototype.setEntityMotion = function (ref, moving) {
        var entity = this.creatureBy(ref);
        if (entity) {
            moving ? entity.moving() : entity.stopped();
        }
    };

    /**
     * Changes the direction an entity is facing.
     *
     * @param ref {number} The internal ID of the entity.
     * @param dir {string} The direction (up, down, left, right).
     */
    World.prototype.changeDirection = function (ref, dir) {
        var entity = this.refs[ref];
        if (entity) {
            entity.setDirection(dir);
        }
    };

    World.prototype.placeEntity = function (id, x, y) {
        this.placeObject(this.entities, id, x, y);
    };

    World.prototype.placeTile = function (id, x, y) {
        this.placeObject(this.map, id, x, y);
    };

    World.prototype.placeObject = function (container, id, x, y) {
        var tile = PIXI.Sprite.fromFrame(id);
        tile.position.set(x, y);
        tile.scale.set(Global.TileScale);

        container.addChild(tile);
    };

    return World;
}]);