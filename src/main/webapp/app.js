'use strict';

var app = angular.module('wsApp', [
    'wsApp.graphics.scene'
]);

/**
 * Various event type identifiers that can be received from the server.
 */
app.constant('Events', {
    Connected: 'ClientConnected',
    Disconnected: 'ClientDisconnected',
    NewSession: 'ClientNewSession',
    MovePlayer: 'MovePlayer',
    AddEntity: 'AddEntity',
    RemoveEntity: 'RemoveEntity',
    DirChange: 'DirectionChange',
    EntityMove: 'EntityMove'
});

app.constant('GameConstants', {
    MoveResult: {
        Valid: 'Valid',
        TooSoon: 'TooSoon',
        Blocked: 'Blocked'
    }
});

/**
 * Factory that provides the client-side interface for communicating with the web server.
 */
app.factory('Client', ['$log', '$timeout', 'Events', function ($log, $timeout, Events) {

    var client = null;
    var connected = false;
    var sessionId = null;
    var listeners = [];

    var scoped = function (func) {
        $timeout(func);
    };

    var dispatchEvent = function (type, data) {
        scoped(function () {
            angular.forEach(listeners, function (listener) {
                listener.onClientEvent(type, data);
            });
        });
    };

    var uuid = function () {
        return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
            var r = crypto.getRandomValues(new Uint8Array(1))[0] % 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
            return v.toString(16);
        });
    };

    return {
        /**
         * Returns whether or not the client is currently connected to the server.
         *
         * @returns {boolean} true if connected, false if not.
         */
        isConnected: function () {
            return connected;
        },

        /**
         * Adds an event listener to notify when server-side events are received.
         *
         * The event listener must have a function property called 'onClientEvent' that
         * receives two parameters:
         *   - type {string} The unique event type identifier.
         *   - data {object} The payload of the event.
         *
         * @param listener
         */
        subscribe: function (listener) {
            if (!listener.hasOwnProperty('onClientEvent')) {
                throw new Error('Listener must have a function property called "onClientEvent"!');
            }

            listeners.push(listener);
        },

        /**
         * Connects to the server and sends the client's registration request.
         */
        connect: function () {
            if (client !== null) {
                $log.warn('Client is already connected');
            }

            else {
                // create a new STOMP client using SockJS
                client = Stomp.over(new SockJS('/topic'));
                client.debug = null;

                // connect to the server-side websockets provider
                client.connect('mike', 'mike', function (data) {
                    $log.debug(data);
                    scoped(function () {
                        connected = true;
                    });

                    // let the app know that we've established a connection
                    dispatchEvent(Events.Connected);

                    // ugh this is way too hackish :(
                    var token = uuid();
                    client.send('/api/user/register', {}, JSON.stringify({
                        name: 'Mike',
                        token: token
                    }));

                    // listen for the server's response to our registration request
                    client.subscribe('/topic/user/' + token + '/register', function (data) {
                        var payload = JSON.parse(data.body);
                        sessionId = payload.sessionId;

                        // listen for events sent out to us only
                        client.subscribe('/topic/user/' + sessionId + '/message', function (data) {
                            var message = JSON.parse(data.body);
                            $log.debug('Received message: ' + message.event);
                            
                            dispatchEvent(message.event, message);
                        });

                        // inform the app that we've got a session set up
                        dispatchEvent(Events.NewSession, {
                            id: sessionId,
                            ref: payload.ref,
                            map: payload.area,
                            entities: payload.entities
                        });
                    });
                });
            }
        },

        /**
         * Sends a player move request to the serer.
         *
         * @param dir {string} The direction in which to move the player.
         */
        sendMove: function (dir) {
            client.send('/api/user/player/move', {}, JSON.stringify({ dir: dir }));
        },

        /**
         * Disconnects a previously established connection to the server.
         */
        disconnect: function () {
            if (client !== null) {
                $log.debug('Disconnecting from server');
                scoped(function () {
                    connected = false;
                });

                // let the app know we've disconnected at this point
                dispatchEvent(Events.Disconnected);
                client.disconnect();
                client = null;
            }

            else {
                $log.warn('Client is already disconnected');
            }
        }
    };
}]);

/**
 * Controller that manages the overall state of the application.
 */
app.controller('AppCtrl', ['$log', 'Client', 'Events', 'GameConstants', function ($log, Client, Events, GameConstants) {

    var self = this;
    this.statusMessage = 'Not connected to anything';
    this.gameMessage = null;
    this.sceneReady = false;
    this.sceneApi = {};

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

            // a new player session has been negotiated
            case Events.NewSession:
                self.statusMessage = 'Opened new client session';
                self.sceneApi.setMap(data);
                break;

            // we've disconnected from the server
            case Events.Disconnected:
                self.statusMessage = 'Not connected to anything';
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
                self.sceneApi.removeEntity(data);
                break;
            
            // an entity has moved on the map
            case Events.EntityMove:
                self.sceneApi.moveEntity(data.ref, data.x, data.y);
                break;
            
            // an entity is now facing a different direction
            case Events.DirChange:
                self.sceneApi.creatureDirChange(data.ref, data.dir);
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
        Client.connect();
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
        Client.subscribe(this);
    };

    this.init();
}]);