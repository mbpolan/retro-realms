'use strict';

var module = angular.module('wsApp.graphics.scene', [
    'wsApp.client.keyboard',
    'wsApp.graphics.assetManager',
    'wsApp.graphics.creature',
    'wsApp.graphics.sprite',
    'wsApp.graphics.world'
]);

module.constant('Global', {
    TileScale: 2,
    TileSize: 16,
    TilesWide: 25,
    TilesHigh: 20
});

/**
 * Directive that maintains the graphics context and underlying game engine.
 */
module.directive('scene', [function () {

    return {
        restrict: 'E',
        scope: {
            api: '=',
            onReady: '&',
            onMotion: '&',
            onPlayerMove: '&',
            onPlayerStopped: '&',
            onFpsCount: '&'
        },
        controller: 'SceneCtrl',
        controllerAs: 'ctrl',
        bindToController: true,
        template: '<div></div>'
    };
}]);

/**
 * Controller that drives the interactions provided by the scene directive.
 */
module.controller('SceneCtrl', [
    '$element', '$log', '$http', 'AssetManager', 'Creature', 'Global', 'Key', 'Keyboard', 'World',
    function ($element, $log, $http, AssetManager, Creature, Global, Key, Keyboard, World) {

        var self = this;
        this.api = this.api || {};
        this.player = null;
        this.isMoving = false;
        this.lastMove = 0;
        this.stage = new PIXI.Container();
        this.assets = new AssetManager();
        this.world = new World(self.stage, self.assets, Global.TilesWide, Global.TilesHigh);
        this.fps = {
            rate: 0,
            frames: 0,
            lastTime: new Date().getTime()
        };

        /**
         * Handler invoked when a keyboard key has been released.
         */
        this.onKeyUp = function () {
            if (self.isMoving) {
                self.isMoving = false;
                self.onPlayerStopped();
            }
        };

        /**
         * Handler invoked when an arrow key has been pressed.
         *
         * @param dir {string} The direction the player is to move (up, down, left, right).
         */
        this.onKeyDown = function (dir) {
            if (!self.isMoving) {
                self.isMoving = true;
                self.onPlayerMove({dir: dir});
            }
        };

        /**
         * Handler invoked when all game assets have been loaded.
         */
        this.onAssetsLoaded = function () {
            $log.info('Scene initialized');

            self.registerKeyHandlers();
            self.onReady();
            self.gameLoop();
        };

        /**
         * Sets up key listeners and prescribes further callbacks for each.
         */
        this.registerKeyHandlers = function () {
            // register listeners for when an arrow key is pressed down
            Keyboard.bind(Key.Up, self.onKeyDown.bind(null, 'up'), self.onKeyUp);
            Keyboard.bind(Key.Down, self.onKeyDown.bind(null, 'down'), self.onKeyUp);
            Keyboard.bind(Key.Left, self.onKeyDown.bind(null, 'left'), self.onKeyUp);
            Keyboard.bind(Key.Right, self.onKeyDown.bind(null, 'right'), self.onKeyUp);
        };

        /**
         * Executes a single iteration of the game loop.
         */
        this.gameLoop = function () {
            // impose a rate limit on how many frames we drawn
            requestAnimationFrame(self.gameLoop);

            // tick any ongoing keyboard events and world state
            Keyboard.tick();
            self.world.tick();

            // draw the next frame of animation of the scene
            self.renderer.render(self.stage);

            // compute the frames-per-second count for this last iteration
            var now = new Date().getTime();
            if (now - self.fps.lastTime > 1000) {
                self.onFpsCount && self.fps.fps !== self.fps.frames && self.onFpsCount({count: self.fps.frames});
                
                self.fps.lastTime = now;
                self.fps.fps = frames;
                self.fps.frames = 0;
            }

            else {
                self.fps.frames++;
            }
        };

        /**
         * Initializes the directive.
         */
        this.init = function () {
            // create a new graphics context and add it to the directive
            self.renderer = PIXI.autoDetectRenderer();
            $element.find('div')[0].appendChild(self.renderer.view);

            self.renderer.render(self.stage);
            self.assets.loadAssets(self.onAssetsLoaded);
        };

        /**
         * Updates the scene with new map data.
         *
         * @param data {object} Tile and entity information for the map.
         */
        this.api.setMap = function (data) {
            console.log('My ref: ' + data.ref);
            self.world.define(data);
            self.player = self.world.creatureBy(data.ref);
        };

        /**
         * Adds a new entity to the map.
         *
         * @param entity {object} Data about the new entity.
         */
        this.api.addEntity = function (entity) {
            self.world.addEntity(entity);
        };

        /**
         * Removes an existing entity from the map.
         *
         * @param ref {number} The internal ID of the entity to remove.
         */
        this.api.removeEntity = function (ref) {
            self.world.removeEntityByRef(ref);
        };

        /**
         * Repositions an existing entity on the map area.
         *
         * @param ref {number} The internal ID of the entity.
         * @param x {number} The new x coordinate.
         * @param y {number} The new y coordinate.
         */
        this.api.moveEntity = function (ref, x, y) {
            self.world.moveEntity(ref, x, y);
        };

        /**
         * Changes the state of an entity to be in motion or standing still.
         *
         * @param ref {number} The internal ID of the entity.
         * @param moving {boolean} true if the entity is now moving, false if stopped.
         */
        this.api.changeEntityMotion = function (ref, moving) {
            self.world.setEntityMotion(ref, moving);
        };

        /**
         * Changes the direction a creature is facing on the map.
         *
         * @param ref {number} The internal ID of the entity.
         * @param dir {string} The direction (up, down, left, right).
         */
        this.api.creatureDirChange = function (ref, dir) {
            self.world.changeDirection(ref, dir);
        };

        /**
         * Displays a chat message sent by a player on the map.
         * 
         * @param ref {number} The internal ID of the player that sent the message.
         * @param text {string} The contents of the message.
         */
        this.api.addPlayerChat = function (ref, text) {
            self.world.addPlayerChat(ref, text);
        };

        // initialize the directive now that we've defined all our of APIs
        this.init();
    }
]);
