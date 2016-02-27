'use strict';

var module = angular.module('wsApp.graphics.assetManager', []);

module.factory('AssetManager', ['$http', function ($http) {

    function AssetManager() {
        this.textureMaps = ['hyrule_tileset', 'character'];
        this.fonts = ['app/assets/nokia.xml'];
        this.unloadedMaps = this.textureMaps.length;
        this.unloadedFonts = this.fonts.length;
    }

    AssetManager.prototype.loadAssets = function (callback) {
        var self = this;

        PIXI.loader.add(self.fonts[0]).load(function () {
            self.unloadedFonts -= self.fonts.length;
            if (self.unloadedFonts === 0 && self.unloadedMaps === 0) {
                callback();
            }
        });

        for (var i = 0; i < self.textureMaps.length; i++) {
            self.loadTextureMap(self.textureMaps[i], function () {
                self.unloadedMaps--;
                if (self.unloadedFonts === 0 && self.unloadedMaps === 0) {
                    callback();
                }
            });
        }
    };

    AssetManager.prototype.parseSpriteSheet = function (data, baseImage) {
        var self = this;

        data.forEach(function (sprite) {
            for (var i = 0; i < sprite.textures.length; i++) {
                var tile = sprite.textures[i];

                var id = sprite.prefix + '-' + tile.id;

                if (angular.isObject(tile.anim)) {
                    var anim = tile.anim;

                    for (var j = 0; j < anim.frames.length; j++) {
                        var frame = anim.frames[j];

                        self.loadTile(id + '-' + j, baseImage,
                            new PIXI.Rectangle(frame.x, frame.y, frame.w, frame.h))
                            .animDelay = anim.delay;
                    }
                }

                else {
                    self.loadTile(id, baseImage,
                        new PIXI.Rectangle(tile.x, tile.y, tile.w, tile.h));
                }
            }
        });
    };

    AssetManager.prototype.loadTextureMap = function (file, callback) {
        var self = this;
        var baseUrl = '/app/assets/' + file;
        var image = baseUrl + '.png';

        PIXI.loader
            .add(image)
            .load(function () {
                var baseImage = PIXI.utils.TextureCache[image];

                $http.get(baseUrl + '.json').then(function (result) {
                    var data = result.data;
                    self.parseSpriteSheet(data, baseImage);

                    callback();
                });
            });
    };

    AssetManager.prototype.loadTile = function (alias, baseImage, rect) {
        var texture = new PIXI.Texture(baseImage, rect);

        // avoid floating point rounding errors on the GPU when scaling textures
        texture.baseTexture.scaleMode = PIXI.SCALE_MODES.NEAREST;

        PIXI.Texture.addTextureToCache(texture, alias);
        return texture;
    };

    return AssetManager;
}]);