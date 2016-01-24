'use strict';

var module = angular.module('wsApp.graphics.creature', ['wsApp.graphics.sprite']);

module.factory('Creature', ['Sprite', function (Sprite) {

    function Creature(parent) {
        Sprite.call(this, parent);

        this.name = null;
        this.setSpeed(1);
        this.setVelocity(0, 0);
    }

    Creature.prototype = Object.create(Sprite.prototype);
    Creature.prototype.constructor = Creature;

    Creature.prototype.animate = function () {
        if (this.isMoving) {
            Sprite.prototype.animate.call(this);
        }
    };

    Creature.prototype.setName = function (text) {
        if (!this.name) {
            this.name = new PIXI.extras.BitmapText(text, {
                font: '8px Nokia Cellphone FC'
            });
            console.log(this.name.width);

            this.name.position.set((this.name.textWidth / 2) - 9, -7);
            this.root.addChild(this.name);
        }

        return this;
    };

    Creature.prototype.moving = function (dir) {
        this.isMoving = true;

        switch (dir) {
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

    Creature.prototype.stopped = function () {
        this.isMoving = false;
        this.resetCurrentAnimation();

        return this;
    };

    Creature.prototype.setDirection = function (dir) {
        this.moving(dir);
        this.isMoving = false;
        return this;
    };

    Creature.prototype.setVelocity = function (vx, vy) {
        this.velocity = { x: vx, y: vy };
        return this;
    };

    Creature.prototype.setSpeed = function (speed) {
        this.speed = speed;
        return this;
    };

    Creature.prototype.moveTo = function (x, y) {
        this.root.x = x;
        this.root.y = y;
        return this;
    };

    Creature.prototype.tick = function () {
        //this.root.x += this.velocity.x * this.speed;
        //this.root.y += this.velocity.y * this.speed;
    };

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