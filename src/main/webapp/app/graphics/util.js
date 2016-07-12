'use strict';

var module = angular.module('wsApp.graphics.util', []);

/**
 * Module that contains various utility functions.
 */
module.service('Util', function () {
    
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
    this.linearInterpolate = function (t0, tf, x0, y0, x1, y1) {
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
});