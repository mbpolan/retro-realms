'use strict';

var module = angular.module('wsApp.scene', []);

module.directive('scene', ['$log', '$http', function ($log, $http) {

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

            var texturesLoaded = function () {
                $log.info('Scene initialized');

                for (var x = 0; x < TILES_WIDE; x++) {
                    for (var y = 0; y < TILES_HIGH; y++) {
                        placeTile('tile-0', x * TILE_SIZE, y * TILE_SIZE);
                    }
                }

                player.pos = { x: 1, y: 1 };
                player.dir = 'down';
                player.sprites = createPlayerSprites();
                setPlayerDirection(player.dir);

                gameLoop();
            };

            var setPlayerDirection = function (dir) {
                player.sprites[player.dir].visible = false;
                player.sprites[dir].visible = true;
                player.dir = dir;
            };

            var createPlayerSprites = function () {
                var sprites = {};
                var textures = new PIXI.Container();

                var dir = ['up', 'down', 'left', 'right'];
                for (var i = 0; i < dir.length; i++) {
                    var sprite = PIXI.Sprite.fromFrame('char-' + dir[i]);
                    sprite.position.set(0, 0);
                    sprite.visible = false;

                    textures.addChild(sprite);
                    sprites[dir[i]] = sprite;
                }

                textures.position.set(1, 1);
                textures.scale.set(TILE_SCALE, TILE_SCALE);
                stage.addChild(textures);

                return sprites;
            };

            var placeTile = function (id, x, y) {
                var tile = PIXI.Sprite.fromFrame(id);
                tile.x = x;
                tile.y = y;
                tile.scale.x = TILE_SCALE;
                tile.scale.y = TILE_SCALE;

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

                renderer.render(stage);
            };

            renderer.render(stage);
            loadAssets();
        }
    }
}]);
