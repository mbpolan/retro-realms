'use strict';

var module = angular.module('wsApp.graphics.scene', [
    'wsApp.graphics.assetManager',
    'wsApp.graphics.creature',
    'wsApp.graphics.sprite',
    'wsApp.graphics.world'
]);

module.constant('Global', {
    TileScale: 2,
    TileSize: 16,
    TilesWide: 25,
    TilesHigh: 20
});

module.directive('scene', [
    '$log', '$http', 'AssetManager', 'Creature', 'Global', 'World',
    function ($log, $http, AssetManager, Creature, Global, World) {

    return {
        restrict: 'E',
        scope: {},
        template: '<div></div>',
        link: function (scope, el) {

            var player = {};
            var renderer = PIXI.autoDetectRenderer();
            var stage = new PIXI.Container();
            var world = new World(stage, Global.TilesWide, Global.TilesHigh);
            var assets = new AssetManager();
            el.find('div')[0].appendChild(renderer.view);

            var registerKeyHandlers = function () {
                kd.UP.down(function () { player.moving('up'); });
                kd.DOWN.down(function () { player.moving('down'); });
                kd.LEFT.down(function () { player.moving('left'); });
                kd.RIGHT.down(function () { player.moving('right'); });

                var onKeyUp = function () { player.stopped(); };
                kd.UP.up(onKeyUp);
                kd.DOWN.up(onKeyUp);
                kd.LEFT.up(onKeyUp);
                kd.RIGHT.up(onKeyUp);
            };

            var assetsLoaded = function () {
                $log.info('Scene initialized');

                registerKeyHandlers();

                // generate an array of 0 (grass) tile ids
                world.define(Array
                    .apply(null, new Array(Global.TilesWide * Global.TilesHigh))
                    .map(function () { return 0; }));

                player = Creature.cardinal(world, 'char', 4)
                    .setSpeed(4)
                    .setName('Mike');

                gameLoop();
            };

            var gameLoop = function () {
                requestAnimationFrame(gameLoop);

                kd.tick();
                player.tick();
                player.animate();

                renderer.render(stage);
            };

            renderer.render(stage);
            assets.loadAssets(assetsLoaded);
        }
    }
}]);
