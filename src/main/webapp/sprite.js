'use strict';

var module = angular.module('wsApp.sprite', []);

module.factory('Sprite', function () {

    function Sprite (parent) {
        this.animations = {};
        this.currentAnim = null;

        this.root = new PIXI.Container();
        this.root.position.set(0, 0);
        this.root.scale.set(2, 2); // FIXME
        parent.addChild(this.root);
    }

    Sprite.prototype = {
        setAnimation: function (name) {
            this.animations[this.currentAnim].visible = false;
            this.animations[name].visible = true;
            this.currentAnim = name;
        },

        addAnimation: function (name, textureIds) {
            var frames = new PIXI.Container();
            frames.position.set(0, 0);
            frames.visible = false;

            for (var i = 0; i < textureIds.length; i++) {
                var sprite = PIXI.Sprite.fromFrame(textureIds[i]);
                sprite.position.set(0, 0);

                frames.addChild(sprite);
            }

            this.root.addChild(frames);

            this.animations[name] = frames;
            this.currentAnim = this.currentAnim || name;

            return this;
        },

        addTo: function () {

            return this;
        },

        setPosition: function (x, y) {
          this.root.position.set(x, y);
        }
    };

    return Sprite;
});