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
        scope: {
            api: '=',
            onReady: '&',
            onPlayerMove: '&'
        },
        template: '<div></div>',
        link: function (scope, el) {

            var player = null;
            var lastMove = 0;
            var renderer = PIXI.autoDetectRenderer();
            var stage = new PIXI.Container();
            var world = new World(stage, Global.TilesWide, Global.TilesHigh);
            var assets = new AssetManager();
            el.find('div')[0].appendChild(renderer.view);

            var registerKeyHandlers = function () {

                var movePlayer = function (dir) {
                    var now = new Date().getTime();

                    // rate limit the player's movement before sending a server request
                    if (now - lastMove > 25) {
                        lastMove = now;
                        player.moving(dir);
                        scope.onPlayerMove({ dir: dir });
                    }
                };

                kd.UP.down(function () { movePlayer('up'); });
                kd.DOWN.down(function () { movePlayer('down'); });
                kd.LEFT.down(function () { movePlayer('left'); });
                kd.RIGHT.down(function () { movePlayer('right'); });

                var onKeyUp = function () { player.stopped(); };
                kd.UP.up(onKeyUp);
                kd.DOWN.up(onKeyUp);
                kd.LEFT.up(onKeyUp);
                kd.RIGHT.up(onKeyUp);
            };

            var assetsLoaded = function () {
                $log.info('Scene initialized');

                registerKeyHandlers();
                scope.onReady();
                gameLoop();
            };

            var gameLoop = function () {
                requestAnimationFrame(gameLoop);

                kd.tick();

                if (player) {
                    player.tick();
                    player.animate();
                }

                renderer.render(stage);
            };

            renderer.render(stage);
            assets.loadAssets(assetsLoaded);

            scope.api = {
                setMap: function (data) {
                    console.log('My ref: ' + data.ref);
                    world.define(data);
                    player = world.creatureBy(data.ref);
                },

                movePlayer: function (x, y) {
                    player.moveTo(x * 8, y * 8);
                },

                addEntity: function (entity) {
                    world.addEntity(entity);
                },

                removeEntity: function (ref) {
                    world.removeEntityByRef(ref);
                },

                moveEntity: function (ref, x, y) {
                    world.moveEntity(ref, x * 8, y * 8);
                },

                creatureDirChange: function (ref, dir) {
                    world.changeDirection(ref, dir);
                }
            }
        }
    }
}]);
