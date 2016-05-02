'use strict';

var module = angular.module('wsApp.client.gameClient', [
    'wsApp.client.chatBox',
    'wsApp.graphics.scene'
]);

/**
 * Directive that visualizes the game client interface.
 */
module.directive('gameClient', [function () {

    return {
        restrict: 'E',
        replace: true,
        scope: true,
        controller: 'GameClientCtrl',
        controllerAs: 'ctrl',
        bindToController: true,
        templateUrl: 'app/client/game-client.html'
    }
}]);

/**
 * Controller that drives the game client interface.
 */
module.controller('GameClientCtrl', [
    '$log', '$window', 'Client', 'Events', 'GameConstants',
    function ($log, $window, Client, Events, GameConstants) {

        var self = this;
        this.statusMessage = 'Not connected to anything';
        this.gameMessage = null;
        this.sceneReady = false;
        this.playerName = 'Mike';
        this.playerColor = 'green';
        this.sceneApi = {};
        this.chatApi = {};
        this.pendingInit = 1;

        /**
         * Handler invoked when a child directive has finished initializing.
         */
        this.onChildInit = function () {
            self.pendingInit--;

            if (self.pendingInit === 0) {
                self.init();
            }
        };

        /**
         * Handler invoked when the user changes their player character's color.
         * 
         * @param color {string} The color to use.
         */
        this.onPlayerColor = function (color) {
            self.playerColor = color;
        };

        /**
         * Handler invoked when the frame rate count changes.
         *
         * @param count The amount of frames drawn per second.
         */
        this.onFpsCount = function (count) {
            self.fps = count;
        };

        /**
         * Handler invoked when a server-side event has been received for this player.
         *
         * @param type {string} The type of event.
         * @param data {object} The payload of the event.
         */
        this.onClientEvent = function (type, data) {
            switch (type) {
                // we've connected to the server
                case Events.Connected:
                    self.statusMessage = 'Connected to server, waiting for session...';
                    break;

                // some sort of connection failure was reported
                case Events.ConnectionFailed:
                    if (data.nameInUse) {
                        self.statusMessage = 'The name you chose is already in use.';
                    }

                    else {
                        self.statusMessage = 'Connection failed! Please try again later.';
                    }

                    break;

                // a new player session has been negotiated
                case Events.NewSession:
                    self.statusMessage = 'Opened new client session';
                    
                    self.sceneApi.setMap(data);
                    self.chatApi.focusElements();
                    break;

                // we've disconnected from the server
                case Events.Disconnected:
                    self.statusMessage = data.error ? 'Connection to server lost' : 'Not connected to anything';
                    break;

                // a player move request was processed
                case Events.MovePlayer:
                    self.processMoveResult(data.result);
                    break;

                // a new entity has appeared on the map
                case Events.AddEntity:
                    self.sceneApi.addEntity(data);
                    break;

                // an existing entity has been removed from the map
                case Events.RemoveEntity:
                    self.sceneApi.removeEntity(data.ref);
                    break;

                // an entity has moved on the map
                case Events.EntityMove:
                    self.sceneApi.moveEntity(data.ref, data.x, data.y);
                    break;

                // an entity has started or stopping moving
                case Events.EntityMotion:
                    self.sceneApi.changeEntityMotion(data.ref, data.start);
                    break;

                // an entity is now facing a different direction
                case Events.DirChange:
                    self.sceneApi.creatureDirChange(data.ref, data.dir);
                    break;
                
                // a player has sent a public chat message
                case Events.PlayerChat:
                    self.chatApi.addMessage(data.name, data.text);
                    this.sceneApi.addPlayerChat(data.ref, data.text);
                    break;

                // unknown event (or unsupported)
                default:
                    $log.warn('Unknown event: ' + type);
                    break;
            }
        };

        /**
         * Handler invoked when the player wants to move their character.
         *
         * @param dir {string} The direction in which to move.
         */
        this.onPlayerMove = function (dir) {
            Client.sendMove(dir);
        };

        /**
         * Handler invoked when the player has stopped moving.
         */
        this.onPlayerStop = function () {
            Client.sendStop();
        };

        /**
         * Determines if the graphics context is ready to be drawn on.
         *
         * @returns {boolean} true if drawing is available, false if not.
         */
        this.isSceneReady = function () {
            return this.sceneReady;
        };

        /**
         * Handler invoked when the server has registered our player's session.
         */
        this.onSceneReady = function () {
            self.sceneReady = true;
        };

        /**
         * Handler invoked when a connection to the server is requested.
         */
        this.onConnect = function () {
            Client.connect(self.playerName, self.playerColor);
        };

        /**
         * Handler invoked when the player wishes to disconnect from the server.
         */
        this.onDisconnect = function () {
            Client.disconnect();
        };

        /**
         * Determines if the client is currently connected to the server.
         *
         * @returns {boolean} true if connected, false if not.
         */
        this.isConnected = function () {
            return Client.isConnected();
        };

        /**
         * Processes the result of a player movement request.
         *
         * @param result {MoveResult} The result of the move request.
         */
        this.processMoveResult = function (result) {
            switch (result) {
                case GameConstants.MoveResult.TooSoon:
                case GameConstants.MoveResult.Valid:
                    self.gameMessage = null;
                    break;
                case GameConstants.MoveResult.Blocked:
                    self.gameMessage = 'You cannot move there';
                    break;
            }
        };

        /**
         * Initializes the controller.
         */
        this.init = function () {
            $log.debug('GameClientCtrl initialized');

            Client.subscribe(this);
        };
    }
]);