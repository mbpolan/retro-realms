'use strict';

var module = angular.module('wsApp.graphics.world', []);

module.factory('World', ['Creature', 'Global', function (Creature, Global) {

    function World(parent, width, height) {
        PIXI.Container.call(this);

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
        var tileSpan = Global.TileSize * Global.TileScale;

        console.log(world);

        for (var x = 0; x < this.scene.w; x++) {
            for (var y = 0; y < this.scene.h; y++) {
                var id = world.map[y * this.scene.w + x];

                this.placeTile('tile-' + id, x * tileSpan, y * tileSpan);
            }
        }

        for (var i = 0; i < world.entities.length; i++) {
            var entity = world.entities[i];

            if (entity.etype === 'Static') {
                this.placeEntity('tile-' + entity.id, entity.x * tileSpan / 4, entity.y * tileSpan / 4);
            }

            else if (entity.etype === 'Creature') {
                console.log(entity.name);
                var creature = Creature.cardinal('char', 4).setName(entity.name);
                creature.moveTo(entity.x * 8, entity.y * 8);
                this.addEntity(creature.getRoot());
            }
        }
    };

    World.prototype.addEntity = function (entity) {
        this.entities.addChild(entity);
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