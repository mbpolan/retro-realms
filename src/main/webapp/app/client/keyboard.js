'use strict';

var module = angular.module('wsApp.client.keyboard', []);

/**
 * Constants for various keyboard keys.
 */
module.constant('Key', {
    Left: 37,
    Up: 38,
    Right: 39,
    Down: 40
});

/**
 * Service that handles capturing persistent keyboard events.
 */
module.factory('Keyboard', ['$document', '$window', function ($document, $window) {

    var listeners = {};
    var activeKeys = {};

    /**
     * Suppresses a keyboard event if at least one listener is bound to it.
     * 
     * @param key {number} The key code in question.
     * @param e {object} The keyboard event.
     */
    var suppressEvent = function (key, e) {
        if (listeners[key] && listeners[key].length > 0) {
            e.preventDefault();
            e.stopPropagation();
        }
    };

    /**
     * Invokes a function on all listeners bound to each active key.
     * 
     * @param f {function} The function to invoke.
     */
    var forEachKey = function (f) {
        for (var key in activeKeys) {
            if (activeKeys.hasOwnProperty(key)) {
                (listeners[key] || []).forEach(function (listener) {
                    f(listener);
                })
            }
        }
    };

    // bind to the keydown handler and start listening for key events
    $document.bind('keydown', function (e) {
        var key = e.which || e.keyCode || 0;
    
        if (!activeKeys[key]) {
            activeKeys[key] = true;

            // first the down handler right away
            (listeners[key] || []).forEach(function (listener) {
                listener.down && listener.down();
            });
        }
    
        suppressEvent(key, e);
    });

    // bind to the keyup handler and listen for when a key was released
    $document.bind('keyup', function (e) {
        var key = e.which || e.keyCode || 0;
    
        delete activeKeys[key];
        (listeners[key] || []).forEach(function (listener) {
            listener.up && listener.up();
        });
    });

    // when the window loses focus, reset all currently active keys
    angular.element($window).bind('blur', function () {
        // notify listeners since the key will need to be pressed again to activate their
        // usual keydown handlers
        forEachKey(function (listener) {
            listener.up && listener.up();
        });

        activeKeys = {};
    });

    return {
        /**
         * Binds a key to handlers for when the key is pressed and then released.
         *
         * @param key {number} The key to bind to.
         * @param down {function} The function to invoke while the key is pressed.
         * @param up {function} The function to invoke once the key is released.
         */
        bind: function (key, down, up) {
            if (down && !angular.isFunction(down)) {
                throw new Error('Keydown parameter must be a function');
            }

            if (up && !angular.isFunction(up)) {
                throw new Error('Keyup parameter must be a function');
            }

            if (!listeners[key]) {
                listeners[key] = [];
            }

            listeners[key].push({ down: down, up: up });
        },

        /**
         * Runs an iteration over the currently pressed keys and invokes handlers.
         */
        tick: function () {
            forEachKey(function (listener) {
                listener.down && listener.down();
            })
        }
    };
}]);
