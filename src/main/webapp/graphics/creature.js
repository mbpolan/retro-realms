'use strict';

var module = angular.module('wsApp.graphics.creature', ['wsApp.sprite']);

module.factory('Creature', ['Sprite', function (Sprite) {

    function Creature(parent) {
        Sprite.call(this, parent);

        this.setSpeed(1);
        this.setVelocity(0, 0);
    }

    Creature.prototype = Object.create(Sprite.prototype);
    Creature.prototype.constructor = Creature;

    Creature.prototype.setVelocity = function (vx, vy) {
        this.velocity = { x: vx, y: vy };
        return this;
    };

    Creature.prototype.setSpeed = function (speed) {
        this.speed = speed;
        return this;
    };

    Creature.prototype.tick = function () {
        this.root.x += this.velocity.x * this.speed;
        this.root.y += this.velocity.y * this.speed;
    };

    Creature.cardinal = function (parent, prefix) {
        var creature = new Creature(parent)
            .addAnimation('up', [prefix + '-up'])
            .addAnimation('down', [prefix + '-down'])
            .addAnimation('right', [prefix + '-right'])
            .addAnimation('left', [prefix + '-left']);

        creature.setAnimation('down');
        return creature;
    };

    return Creature;
}]);