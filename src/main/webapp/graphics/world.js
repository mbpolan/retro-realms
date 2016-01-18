'use strict';

var module = angular.module('wsApp.graphics.world', []);

module.factory('World', ['Global', function (Global) {

    function World(parent, width, height) {
        PIXI.Container.call(this);

        this.scene = {w: width, h: height};
        parent.addChild(this);
    }

    World.prototype = Object.create(PIXI.Container.prototype);
    World.prototype.constructor = World;

    World.prototype.define = function (tiles) {
        this.removeChildren();
        var tileSpan = Global.TileSize * Global.TileScale;

        for (var x = 0; x < this.scene.w; x++) {
            for (var y = 0; y < this.scene.h; y++) {
                var id = tiles[y * this.scene.w + x];

                this.placeTile('tile-' + id, x * tileSpan, y * tileSpan);
            }
        }
    };

    World.prototype.placeTile = function (id, x, y) {
        var tile = PIXI.Sprite.fromFrame(id);
        tile.position.set(x, y);
        tile.scale.set(Global.TileScale);

        this.addChild(tile);
    };

    return World;
}]);