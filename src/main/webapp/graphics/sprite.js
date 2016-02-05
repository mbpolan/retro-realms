'use strict';

var module = angular.module('wsApp.graphics.sprite', []);

module.constant('SpriteConstants', {
    Scale: 2
});

module.factory('Sprite', ['SpriteConstants', function (SpriteConstants) {
    
    function Sprite(parent) {
        this.animations = {};
        this.currentAnim = null;
        this.animated = false;
        this.lastFrame = 0;
        this.currentFrame = 0;

        this.root = new PIXI.Container();
        this.root.position.set(0, 0);
        this.root.scale.set(SpriteConstants.Scale, SpriteConstants.Scale);

        if (angular.isObject(parent)) {
            parent.addChild(this.root);
        }
    }

    Sprite.prototype = {
        resetCurrentAnimation: function () {
            var current = this.animations[this.currentAnim];
            current.chain[this.currentFrame].visible = false;
            current.chain[0].visible = true;
        },

        setAnimation: function (name) {
            // are we switching to a new animation?
            if (this.currentAnim !== name) {
                var current = this.animations[this.currentAnim];

                // hide the current animation
                current.visible = false;
                for (var i = 0; i < current.chain.length; i++) {
                    current.chain[i].visible = false;
                }

                // show the new animation
                this.animations[name].visible = true;
                this.currentAnim = name;
                this.currentFrame = 0;
                this.animations[name].chain[this.currentFrame].visible = true;
            }
        },

        addAnimation: function (name, frameId, count) {
            var frames = new PIXI.Container();
            frames.chain = [];
            frames.position.set(0, 0);
            frames.visible = false;

            var realCount = count || 1;
            this.animated = angular.isDefined(count);

            for (var i = 0; i < realCount; i++) {
                // load the texture for this particular frame
                var textureId = this.animated ? frameId + '-' + i : frameId;
                var texture = PIXI.utils.TextureCache[textureId];
                if (!texture) {
                    throw new Error('The frameId "' + textureId + '" does not exist in the texture cache');
                }

                var sprite = new PIXI.Sprite(texture);
                sprite.position.set(0, 0);
                sprite.animDelay = texture.animDelay;

                // only show the first frame in a sequence of frames
                sprite.visible = (i === 0);

                frames.addChild(sprite);
                frames.chain.push(sprite);
            }

            this.root.addChild(frames);

            this.animations[name] = frames;
            this.currentAnim = this.currentAnim || name;

            return this;
        },

        setPosition: function (x, y) {
            this.root.position.set(x, y);
        },

        getRoot: function () {
            return this.root;
        },

        animate: function () {
            var frames = this.animations[this.currentAnim];
            var thisFrame = frames.chain[this.currentFrame];

            // is it time to switch to the next frame?
            var now = Date.now();
            if (now - this.lastFrame > thisFrame.animDelay) {
                thisFrame.visible = false;
                this.currentFrame = ++this.currentFrame % frames.chain.length;

                frames.chain[this.currentFrame].visible = true;
                this.lastFrame = now;
            }
        }
    };

    return Sprite;
}]);