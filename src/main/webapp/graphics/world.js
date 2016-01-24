'use strict';

var module = angular.module('wsApp.graphics.world', []);

module.factory('World', ['Creature', 'Global', function (Creature, Global) {

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

    World.prototype.define = function (world) {
        this.map.removeChildren();
        this.entities.removeChildren();

        console.log(world);

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

    World.prototype.creatureBy = function (ref) {
        return this.refs[ref];
    };

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