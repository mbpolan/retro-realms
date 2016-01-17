'use strict';

var module = angular.module('wsApp.scene', []);

module.directive('scene', ['$log', '$http', function ($log, $http) {

    return {
        restrict: 'E',
        scope: {},
        template: '<div></div>',
        link: function (scope, el) {
            var TILE_SIZE = 16;
            var TILE_SCALE = 2;
            var TILES_WIDE = 15;
            var TILES_HIGH = 15;

            var renderer = PIXI.autoDetectRenderer();
            var stage = new PIXI.Container();
            el.find('div')[0].appendChild(renderer.view);

            var texturesLoaded = function () {
                $log.info('Scene initialized');

                for (var x = 0; x < TILES_WIDE; x++) {
                    for (var y = 0; y < TILES_HIGH; y++) {
                        placeTile('0', x * TILE_SIZE, y * TILE_SIZE);
                    }
                }

                gameLoop();
            };

            var placeTile = function (id, x, y) {
                var tile = PIXI.Sprite.fromFrame(id);
                tile.x = x;
                tile.y = y;
                tile.scale.x = TILE_SCALE;
                tile.scale.y = TILE_SCALE;

                stage.addChild(tile);
            };

            var loadTextureMap = function (file, callback) {
                var baseUrl = '/assets/' + file;
                var image = baseUrl + '.png';

                PIXI.loader
                    .add(image)
                    .load(function () {
                        var baseImage = PIXI.utils.TextureCache[image];

                        $http.get(baseUrl + '.json').then(function (result) {
                            for (var key in result.data) {
                                if (result.data.hasOwnProperty(key)) {
                                    var tile = result.data[key];
                                    loadTile(tile.id.toString(), baseImage,
                                        new PIXI.Rectangle(tile.x, tile.y, tile.w, tile.h));
                                }
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
            loadTextureMap('hyrule_tileset', texturesLoaded);
        }
    }
}]);
