'use strict';

var module = angular.module('wsApp.scene', [
    'wsApp.graphics.creature',
    'wsApp.sprite'
]);

module.directive('scene', ['$log', '$http', 'Creature', 'Sprite', function ($log, $http, Creature, Sprite) {

    var TILE_BASE_SIZE = 16;
    var TILE_SCALE = 2;
    var TILE_SIZE = TILE_BASE_SIZE * TILE_SCALE;
    var TILES_WIDE = 15;
    var TILES_HIGH = 15;

    return {
        restrict: 'E',
        scope: {},
        template: '<div></div>',
        link: function (scope, el) {

            var textureMaps = ['hyrule_tileset', 'character'];
            var unloadedMaps = textureMaps.length;
            var player = {};

            var renderer = PIXI.autoDetectRenderer();
            var stage = new PIXI.Container();
            el.find('div')[0].appendChild(renderer.view);

            var onKeyUp = function () {
                player.setVelocity(0, 0);
            };

            var registerKeyHandlers = function () {
                kd.UP.down(function () {
                    player.setVelocity(0, -1);
                    player.setAnimation('up');
                });

                kd.DOWN.down(function () {
                    player.setVelocity(0, 1);
                    player.setAnimation('down');
                });

                kd.LEFT.down(function () {
                    player.setVelocity(-1, 0);
                    player.setAnimation('left');
                });

                kd.RIGHT.down(function () {
                    player.setVelocity(1, 0);
                    player.setAnimation('right');
                });

                kd.UP.up(onKeyUp);
                kd.DOWN.up(onKeyUp);
                kd.LEFT.up(onKeyUp);
                kd.RIGHT.up(onKeyUp);
            };

            var texturesLoaded = function () {
                $log.info('Scene initialized');

                registerKeyHandlers();

                for (var x = 0; x < TILES_WIDE; x++) {
                    for (var y = 0; y < TILES_HIGH; y++) {
                        placeTile('tile-0', x * TILE_SIZE, y * TILE_SIZE);
                    }
                }

                player = Creature.cardinal(stage, 'char').setSpeed(3);

                gameLoop();
            };

            var placeTile = function (id, x, y) {
                var tile = PIXI.Sprite.fromFrame(id);
                tile.position.set(x, y);
                tile.scale.set(TILE_SCALE);

                stage.addChild(tile);
            };

            var loadAssets = function () {
                for (var i = 0; i < textureMaps.length; i++) {
                    loadTextureMap(textureMaps[i], function () {
                        unloadedMaps--;
                        if (unloadedMaps === 0) {
                            texturesLoaded();
                        }
                    });
                }
            };

            var loadTextureMap = function (file, callback) {
                var baseUrl = '/assets/' + file;
                var image = baseUrl + '.png';

                PIXI.loader
                    .add(image)
                    .load(function () {
                        var baseImage = PIXI.utils.TextureCache[image];

                        $http.get(baseUrl + '.json').then(function (result) {
                            var data = result.data;
                            console.log(data);

                            for (var i = 0; i < data.textures.length; i++) {
                                var tile = data.textures[i];

                                loadTile(data.prefix + '-' + tile.id, baseImage,
                                    new PIXI.Rectangle(tile.x, tile.y, tile.w, tile.h));
                            }

                            callback();
                        });
                    });
            };

            var loadTile = function (alias, baseImage, rect) {
                var texture = new PIXI.Texture(baseImage, rect);

                // avoid floating point rounding errors on the GPU when scaling textures
                texture.baseTexture.scaleMode = PIXI.SCALE_MODES.NEAREST;

                PIXI.Texture.addTextureToCache(texture, alias);
            };

            var gameLoop = function () {
                requestAnimationFrame(gameLoop);

                kd.tick();
                player.tick();

                renderer.render(stage);
            };

            renderer.render(stage);
            loadAssets();
        }
    }
}]);
