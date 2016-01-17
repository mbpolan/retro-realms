'use strict';

var module = angular.module('wsApp.graphics.creature', ['wsApp.sprite']);

module.factory('Creature', ['Sprite', function (Sprite) {

    function Creature(parent) {
        Sprite.call(this, parent);

        this.setVelocity(0, 0);
    }

    Creature.prototype = Object.create(Sprite.prototype);
    Creature.prototype.constructor = Creature;

    Creature.prototype.setVelocity = function (vx, vy) {
        this.velocity = { x: vx, y: vy };
    };

    return Creature;
}]);