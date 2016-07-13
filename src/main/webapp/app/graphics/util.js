'use strict';

var module = angular.module('wsApp.graphics.util', []);

/**
 * Module that contains various utility functions.
 */
module.service('Util', function () {

    // template for animation containers
    var Animation = {
        done: false,
        active: function () {
            return !this.done;
        },
        animate: function () {
            return this.algorithm();
        }
    };

    /**
     * Performs linear interpolation on a set of starting and ending 2D coordinates.
     *
     * @param t0 {number} The start time of the animation.
     * @param tf {number} The expected end time of the animation.
     * @param x0 {number} The starting x coordinate.
     * @param y0 {number} The starting y coordinate.
     * @param x1 {number} The ending x coordinate.
     * @param y1 {number} The ending y coordinate.
     * @returns {{done: boolean, x: *, y: *}} Object containing the results of linear interpolation.
     */
    var linearInterpolate = function (t0, tf, x0, y0, x1, y1) {
        var t1 = new Date().getTime();
        var ratio = (t1 - t0) / tf;

        var x = x0 + ((x1 - x0) * ratio);
        var y = y0 + ((y1 - y0) * ratio);
        var done = false;

        if (ratio >= 1) {
            done = true;
            x = x1;
            y = y1;
        }

        return {
            done: done,
            x: x,
            y: y
        };
    };
    
    /**
     * Creates an animation that interpolates a container's position over time.
     *
     * @param container {Container} A PIXI container to animate.
     * @param x {number} The target x coordinate to move the object to.
     * @param y {number} The target y coordinate to move the object to.
     * @param time {number} The total duration of the animation, in milliseconds.
     * @returns {object} An animation container.
     */
    this.linearInterpolation = function (container, x, y, time) {
        return angular.extend({
            x0: container.x,
            y0: container.y,
            x1: x,
            y1: y,
            t0: new Date().getTime(),
            tf: time,
            algorithm: function () {
                var result = linearInterpolate(this.t0, this.tf, this.x0, this.y0, this.x1, this.y1);
                container.x = result.x;
                container.y = result.y;

                this.done = result.done;
                return result;
            }
        }, Animation);
    };
});