'use strict';

var module = angular.module('wsApp.graphics.creature', ['wsApp.graphics.sprite']);

/**
 * Factory that creates models for creatures on the game world.
 */
module.factory('Creature', ['Sprite', 'SpriteConstants', function (Sprite, SpriteConstants) {

    /**
     * Creates a new creature sprite.
     *
     * @param parent The parent canvas element.
     * @constructor Initializes a new creature sprite.
     */
    function Creature(parent) {
        Sprite.call(this, parent);

        this.x = 0;
        this.y = 0;
        this.name = null;
        this.setSpeed(1);
        this.setVelocity(0, 0);
    }

    Creature.prototype = Object.create(Sprite.prototype);
    Creature.prototype.constructor = Creature;

    /**
     * Progresses the animation of this creature's current frame set.
     */
    Creature.prototype.animate = function () {
        if (this.isMoving) {
            Sprite.prototype.animate.call(this);
        }
    };

    /**
     * Sets the visible name tag for this creature.
     *
     * @param text {string} The creature's name.
     * @returns {Creature} This instance.
     */
    Creature.prototype.setName = function (text) {
        if (!this.name) {
            this.name = new PIXI.Text(text, {
                font: 'bold 18px Arial',
                fill: 'green',
                stroke: 'black',
                strokeThickness: 4,
                miterLimit: 1
            });

            // position the creature's name above its sprite
            var x = (this.root.width / (SpriteConstants.Scale * 2)) - (this.name.width / (SpriteConstants.Scale * 2));
            var y = -(this.name.height / SpriteConstants.Scale);
            this.name.position.set(x, y);

            // scale the text down since the sprite is already upscaled
            this.name.scale.set(1 / SpriteConstants.Scale, 1 / SpriteConstants.Scale);
            this.root.addChild(this.name);
        }

        return this;
    };

    /**
     * Marks the creature as in motion in a particular direction.
     *
     * @param dir {string=} Optional - the new movement direction (up, down, left, right).
     * @returns {Creature} This instance.
     */
    Creature.prototype.moving = function (dir) {
        this.isMoving = true;
        return this;
    };

    /**
     * Marks this creature as no longer moving.
     *
     * @returns {Creature} This instance.
     */
    Creature.prototype.stopped = function () {
        this.isMoving = false;
        this.resetCurrentAnimation();

        return this;
    };

    /**
     * Sets the direction the creature is facing.
     *
     * @param dir {string} The direction (up, down, left, right).
     * @returns {Creature} This instance.
     */
    Creature.prototype.setDirection = function (dir) {
        var direction = dir || this.currentAnim;
        switch (direction) {
            case 'up':
                this.setAnimation('up');
                break;
            case 'down':
                this.setAnimation('down');
                break;
            case 'left':
                this.setAnimation('left');
                break;
            case 'right':
                this.setAnimation('right');
                break;
            default:
                throw new Error('Unknown creature direction: "' + dir + '"');
        }

        return this;
    };

    /**
     * Sets the velocity at which this creature moves across the map.
     *
     * @param vx {number} The velocity along the x-axis.
     * @param vy {number} The velocity along the y-axis.
     * @returns {Creature} This instance.
     */
    Creature.prototype.setVelocity = function (vx, vy) {
        this.velocity = { x: vx, y: vy };
        return this;
    };

    /**
     * Sets the speed at which this creaures moves.
     * 
     * @param speed {number} The number of units this creature moves per tick.
     * @returns {Creature} This instance.
     */
    Creature.prototype.setSpeed = function (speed) {
        this.speed = speed;
        return this;
    };

    /**
     * Places the creature on the map at a specified location immediately.
     *
     * @param x {number} The x coordinate.
     * @param y {number} The y coordinate.
     * @returns {Creature} This instance.
     */
    Creature.prototype.placeAt = function (x, y) {
        this.x = x;
        this.y = y;
        this.root.x = this.x * 8;
        this.root.y = this.y * 8;
        return this;
    };

    /**
     * Transitions the creature to the specified location on the map with animation.
     * 
     * @param x {number} The x coordinate.
     * @param y {number} The y coordinate.
     * @returns {Creature} This instance.
     */
    Creature.prototype.moveTo = function (x, y) {
        this.x0 = this.x;
        this.y0 = this.y;
        this.x1 = x;
        this.y1 = y;

        this.t0 = new Date().getTime();
        this.tf = 40;
        this.moveAnim = true;

        return this;
    };

    /**
     * Computes the creature's new properties on each iteration of the game loop.
     */
    Creature.prototype.tick = function () {
        this.animate();

        if (this.moveAnim) {
            var t1 = new Date().getTime();
            var ratio = (t1 - this.t0) / this.tf;

            var x = this.x0 + ((this.x1 - this.x0) * ratio);
            var y = this.y0 + ((this.y1 - this.y0) * ratio);
            this.x = x;
            this.y = y;

            this.root.x = x * 8;
            this.root.y = y * 8;

            if (ratio >= 1) {
                this.moveAnim = false;
                this.x = this.x1;
                this.y = this.y1;

                this.root.x = this.x1 * 8;
                this.root.y = this.y1 * 8;
            }
        }
    };

    /**
     * Builds that creates a new creature with properties.
     * 
     * @param prefix {string} The prefix for the creature's animations.
     * @param assets {AssetManager} The asset manager to use for loading animations. 
     * @param parent The parent canvas element.
     * @returns {Creature} A new instance of this object.
     */
    Creature.cardinal = function (prefix, assets, parent) {
        var creature = new Creature(parent);

        // load animations for the four cardinal directions
        ['up', 'down', 'left', 'right'].forEach(function (dir) {
            var animName = prefix + '-' + dir;
            creature.addAnimation(dir, animName, assets.getNumFrames(animName));
        });

        creature.setAnimation('down');
        return creature;
    };

    return Creature;
}]);