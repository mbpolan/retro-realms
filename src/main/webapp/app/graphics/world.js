'use strict';

var module = angular.module('wsApp.graphics.world', ['wsApp.graphics.util']);

/**
 * Service that primarily manages and coordinates graphics on the scene.
 */
module.factory('World', ['$timeout', 'Creature', 'Global', 'Util', function ($timeout, Creature, Global, Util) {

    /**
     * Creates a new viewable "world" view of the game map.
     *
     * @param parent The parent canvas element.
     * @param assets {AssetManager} The asset manager to use for loading graphics.
     * @param width The width of the map, in tiles.
     * @param height The height of the map, in tiles.
     * @constructor Creates a new map canvas.
     */
    function World(parent, assets, width, height) {
        PIXI.Container.call(this);

        this.refs = {};
        this.assets = assets;
        this.tileSpan = Global.TileSize * Global.TileScale;
        this.tilesWide = 0;
        this.tilesHigh = 0;
        this.map = new PIXI.Container();
        this.entities = new PIXI.Container();
        this.sceneWidth = 0;
        this.sceneHeight = 0;
        this.addChild(this.map);
        this.addChild(this.entities);

        this.scene = {w: width, h: height};
        parent.addChild(this);
    }

    World.prototype = Object.create(PIXI.Container.prototype);
    World.prototype.constructor = World;

    /**
     * Sets the size of the containing scene.
     *
     * @param width {number} The width of the scene, in pixels.
     * @param height {number} The height of the scene, in pixels.
     */
    World.prototype.setSceneSize = function (width, height) {
        this.sceneWidth = width;
        this.sceneHeight = height;
    };

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

        // save the dimensions of this map
        this.tilesWide = world.tilesWide;
        this.tilesHigh = world.tilesHigh;

        // place the tiles of the map on each coordinate space
        for (var x = 0; x < world.tilesWide; x++) {
            for (var y = 0; y < world.tilesHigh; y++) {
                var id = world.map[y * world.tilesWide + x];

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
        if (this.moveAnim) {
            var result = Util.linearInterpolate(this.t0, this.tf, this.x0, this.y0, this.x1, this.y1);

            this.x = result.x;
            this.y = result.y;
            this.moveAnim = !result.done;
        }

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
     * Sorts the entities on the scene in descending order of y-axis coordinates.
     */
    World.prototype.sortEntities = function () {
        this.entities.children.sort(function (a, b) {
            return (a.position.y - b.position.y);
        });
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
            var creature = Creature.cardinal(entity.id, this.assets)
                .setName(entity.name)
                .setDirection(entity.dir)
                .placeAt(entity.x, entity.y);
            
            // add the entity to the entities container and resort it
            this.entities.addChild(creature.getRoot());
            this.sortEntities();

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
        var entity = this.refs[ref];
        if (entity) {
            this.entities.removeChild(entity.getRoot());
            delete this.refs[ref];
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
     * @param adjustMap {boolean} true to translate the world to center around the entity, false otherwise.
     */
    World.prototype.moveEntity = function (ref, x, y, adjustMap) {
        var entity = this.creatureBy(ref);
        if (entity) {
            // move the entity and recompute sorting attributes
            entity.moveTo(x, y);
            this.sortEntities();
        }

        // position the map so that it's centered around this entity if requested
        if (adjustMap) {
            this.centerAround(x * 8, y * 8);
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

    /**
     * Adds text for a chat message sent by a player.
     *
     * @param ref {number} The internal ID of the player that sent the message.
     * @param text {string} The contents of the message.
     */
    World.prototype.addPlayerChat = function (ref, text) {
        var entity = this.refs[ref];
        if (entity) {
            // create a new text element
            var msg = new PIXI.Text(text, {
                font: '20px Arial bold',
                fill: 'yellow',
                stroke: 'black',
                strokeThickness: 1
            });

            // position it above the player that sent the message
            var pos = entity.getPosition();
            var x = pos.x + (entity.getRoot().width / 2) - (msg.width / 2);
            var y = pos.y - 30; // FIXME
            
            msg.position.set(Math.max(0, x), Math.max(0, y));
            this.addChild(msg);

            // have the text disappear after a few seconds
            var self = this;
            $timeout(function () {
                self.removeChild(msg);
            }, 2500);
        }
    };

    /**
     * Centers the map to be focused around a particular entity.
     *
     * @param x The x coordinate of the entity.
     * @param y The y coordinate of the entity.
     */
    World.prototype.centerAround = function (x, y) {
        // compute the pixel height of the map area
        var mapHeight = this.tilesHigh * this.tileSpan;
        var mapWidth = this.tilesWide * this.tileSpan;

        var dt = y - (this.sceneHeight / 2);
        var y1 = dt < 0 ? 0 : Math.min(mapHeight - this.sceneHeight, dt) * -1;

        var du = x - (this.sceneWidth / 2);
        var x1 = du < 0 ? 0 : Math.min(mapWidth - this.sceneWidth, du) * -1;

        this.x0 = this.x;
        this.y0 = this.y;
        this.x1 = x1;
        this.y1 = y1;

        this.t0 = new Date().getTime();
        this.tf = 40;
        this.moveAnim = true;
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