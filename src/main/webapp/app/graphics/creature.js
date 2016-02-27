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
     * Positions the creature at a new location on the scene.
     * 
     * @param x {number} The x coordinate.
     * @param y {number} The y coordinate.
     * @returns {Creature} This instance.
     */
    Creature.prototype.moveTo = function (x, y) {
        this.root.x = x;
        this.root.y = y;
        return this;
    };

    /**
     * Computes the creature's new properties on each iteration of the game loop.
     */
    Creature.prototype.tick = function () {
        this.animate();
    };

    /**
     * Builds that creates a new creature with properties.
     * 
     * @param prefix {string} The prefix for the creature's animations.
     * @param frames {number} The amount of frames in each animation. 
     * @param parent The parent canvas element.
     * @returns {Creature} A new instance of this object.
     */
    Creature.cardinal = function (prefix, frames, parent) {
        var creature = new Creature(parent)
            .addAnimation('up', prefix + '-up', frames)
            .addAnimation('down', prefix + '-down', frames)
            .addAnimation('right', prefix + '-right', frames)
            .addAnimation('left', prefix + '-left', frames);

        creature.setAnimation('down');
        return creature;
    };

    return Creature;
}]);