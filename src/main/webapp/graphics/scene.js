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

/**
 * Directive that maintains the graphics context and underlying game engine.
 */
module.directive('scene', [
    '$log', '$http', 'AssetManager', 'Creature', 'Global', 'World',
    function ($log, $http, AssetManager, Creature, Global, World) {

    return {
        restrict: 'E',
        scope: {
            api: '=',
            onReady: '&',
            onMotion: '&',
            onPlayerMove: '&'
        },
        template: '<div></div>',
        link: function (scope, el) {

            var player = null;
            var isMoving = false;
            var lastMove = 0;
            var renderer = PIXI.autoDetectRenderer();
            var stage = new PIXI.Container();
            var world = new World(stage, Global.TilesWide, Global.TilesHigh);
            var assets = new AssetManager();
            el.find('div')[0].appendChild(renderer.view);

            var onKeyUp = function () {
                if (isMoving) {
                    isMoving = false;
                    scope.onMotion({ moving: false });
                }
            };

            var onKeyDown = function (dir) {
                if (!isMoving) {
                    isMoving = true;
                    scope.onMotion({ moving: true });
                }

                var now = new Date().getTime();

                // rate limit the player's movement before sending a server request
                if (now - lastMove > 25) {
                    lastMove = now;
                    // player.moving(dir);
                    scope.onPlayerMove({ dir: dir });
                }
            };

            /**
             * Sets up key listeners and prescribes further callbacks for each.
             */
            var registerKeyHandlers = function () {
                // register listeners for when an arrow key is pressed down
                kd.UP.down(onKeyDown.bind(null, 'up'));
                kd.DOWN.down(onKeyDown.bind(null, 'down'));
                kd.LEFT.down(onKeyDown.bind(null, 'left'));
                kd.RIGHT.down(onKeyDown.bind(null, 'right'));

                // register listeners for when an arrow key is released
                kd.UP.up(onKeyUp);
                kd.DOWN.up(onKeyUp);
                kd.LEFT.up(onKeyUp);
                kd.RIGHT.up(onKeyUp);
            };

            /**
             * Callback invoked when all game assets have been loaded.
             */
            var assetsLoaded = function () {
                $log.info('Scene initialized');

                registerKeyHandlers();
                scope.onReady();
                gameLoop();
            };

            /**
             * Executes a single iteration of the game loop.
             */
            var gameLoop = function () {
                // impose a rate limit on how many frames we drawn
                requestAnimationFrame(gameLoop);

                // tick any ongoing keyboard events and world state
                kd.tick();
                world.tick();

                // draw the next frame of animation of the scene
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

                addEntity: function (entity) {
                    world.addEntity(entity);
                },

                removeEntity: function (ref) {
                    world.removeEntityByRef(ref);
                },

                moveEntity: function (ref, x, y) {
                    world.moveEntity(ref, x * 8, y * 8);
                },

                changeEntityMotion: function (ref, moving) {
                    // console.log(ref + ' moving? ' + moving);
                    world.setEntityMotion(ref, moving);
                },

                creatureDirChange: function (ref, dir) {
                    world.changeDirection(ref, dir);
                }
            }
        }
    }
}]);
