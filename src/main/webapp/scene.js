'use strict';

var module = angular.module('wsApp.scene', []);

module.directive('scene', ['$log', '$http', function ($log, $http) {

    return {
        restrict: 'E',
        scope: {},
        template: '<div></div>',
        link: function (scope, el, attrs) {
            var renderer = PIXI.autoDetectRenderer();
            el.find('div')[0].appendChild(renderer.view);

            var texturesLoaded = function () {
                $log.info('Scene initialized');

                var tileSize = 32;
                var tilesWide = 10;
                var tilesHigh = 10;

                for (var x = 0; x < tilesWide; x++) {
                    for (var y = 0; y < tilesHigh; y++) {
                        placeTile('grass', x * tileSize, y * tileSize);
                    }
                }
            };

            var placeTile = function (id, x, y) {
                var tile = PIXI.Sprite.fromFrame(id);
                tile.x = x;
                tile.y = y;
                tile.scale.x = 2;
                tile.scale.y = 2;

                stage.addChild(tile);
                renderer.render(stage);
            };

            var loadTextureMap = function (file, callback) {
                var baseUrl = '/assets/' + file;
                var image = baseUrl + '.png';

                PIXI.loader
                    .add(image)
                    .load(function () {
                        var baseImage = PIXI.TextureCache[image];

                        $http.get(baseUrl + '.json').then(function (result) {
                            for (var key in result.data) {
                                if (result.data.hasOwnProperty(key)) {
                                    var tile = result.data[key];
                                    loadTile(key, baseImage, new PIXI.Rectangle(tile.x, tile.y, tile.w, tile.h));
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

            var stage = new PIXI.Container();
            renderer.render(stage);

            loadTextureMap('hyrule_tileset', texturesLoaded);
        }
    }
}]);
