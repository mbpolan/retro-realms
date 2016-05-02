'use strict';

var app = angular.module('wsApp', [
    'wsApp.client.gameClient',
    'ui.bootstrap'
]);

/**
 * Various event type identifiers that can be received from the server.
 */
app.constant('Events', {
    Connected: 'ClientConnected',
    Disconnected: 'ClientDisconnected',
    ConnectionFailed: 'ConnectionFailed',
    NewSession: 'ClientNewSession',
    MovePlayer: 'MovePlayer',
    AddEntity: 'AddEntity',
    RemoveEntity: 'RemoveEntity',
    DirChange: 'DirectionChange',
    EntityMove: 'EntityMove',
    EntityMotion: 'EntityMotion',
    PlayerChat: 'PlayerChat'
});

/**
 * Constants that map to particular protocol message values.
 */
app.constant('GameConstants', {
    ConnectResult: {
        Valid: 'Valid',
        NameInUse: 'NameInUse'
    },
    MoveResult: {
        Valid: 'Valid',
        TooSoon: 'TooSoon',
        Blocked: 'Blocked'
    }
});

/**
 * An event bus that dispatches events to interested listeners.
 */
app.factory('EventBus', [function () {
    
    var listeners = [];
    
    return {
        /**
         * Registers a new listener to be notified of events.
         * 
         * @param listener {function} The function to invoked.
         * @param events {Array=} Array of events to be notified of, or undefined for all events.
         */
        register: function (listener, events) {
            listeners.push({
                callback: listener,
                events: events
            });
        },

        /**
         * Dispatches an event to listeners that have registered for that event type.
         * 
         * @param type {string} The type of event.
         * @param data {object} The payload of the event.
         */
        dispatch: function (type, data) {
            listeners.forEach(function (listener) {
                if (!angular.isDefined(listener.events) || listener.events.indexOf(type) > 0) {
                    listener.callback(type, data);
                }
            });
        }
    };
}]);

/**
 * Factory that provides the client-side interface for communicating with the web server.
 */
app.factory('Client', ['$log', '$timeout', 'Events', 'GameConstants', function ($log, $timeout, Events, GameConstants) {

    var client = null;
    var connected = false;
    var sessionId = null;
    var listeners = [];

    /**
     * Runs a function in the context of the AngularJS event loop.
     *
     * You may modify scoped variables inside the function.
     *
     * @param func {function} The function to run.
     */
    var scoped = function (func) {
        $timeout(func);
    };

    /**
     * Sends an event to all registered listeners.
     *
     * @param type The identifier for the event.
     * @param data The payload for the event.
     */
    var dispatchEvent = function (type, data) {
        scoped(function () {
            angular.forEach(listeners, function (listener) {
                listener.onClientEvent(type, data);
            });
        });
    };

    /**
     * Generates a mostly unique UUID for requesting new sessions.
     *
     * @returns {string} A random UUID string.
     */
    var uuid = function () {
        return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
            var r = crypto.getRandomValues(new Uint8Array(1))[0] % 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
            return v.toString(16);
        });
    };

    /**
     * Processes the server's response to a connection request.
     *
     * @param name {string} The user name to request.
     * @param color {string} The color for the player's character.
     * @param instance {object} The instance of the factory.
     */
    var processConnection = function (name, color, instance) {
        scoped(function () {
            connected = true;
        });

        // let the app know that we've established a connection
        dispatchEvent(Events.Connected);

        // ugh this is way too hackish :(
        var token = uuid();
        client.send('/api/user/register', {}, JSON.stringify({
            name: name,
            color: color,
            token: token
        }));

        // listen for the server's response to our registration request
        client.subscribe('/topic/user/' + token + '/register', function (data) {
            var payload = JSON.parse(data.body);

            switch (payload.result) {
                // the connection succeeded and we were registered
                case GameConstants.ConnectResult.Valid:
                    var session = payload.session;
                    sessionId = session.sessionId;

                    // listen for events sent out to us only
                    client.subscribe('/topic/user/' + sessionId + '/message', function (data) {
                        var message = JSON.parse(data.body);
                        // $log.debug('Received message: ' + message.event);

                        dispatchEvent(message.event, message);
                    });

                    // inform the app that we've got a session set up
                    dispatchEvent(Events.NewSession, {
                        id: sessionId,
                        ref: session.ref,
                        map: session.area,
                        entities: session.entities
                    });

                    break;

                // the request was rejected by the server
                case GameConstants.ConnectResult.NameInUse:
                    instance.disconnect();

                    dispatchEvent(Events.ConnectionFailed, {
                        nameInUse: true
                    });
                    break;

                default:
                    $log.error('Unknown connection result: ' + data.result);
            }
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
         * 
         * @param name {string} The name of the player.
         * @param color {string} The color for the player's character.
         */
        connect: function (name, color) {
            if (client !== null) {
                $log.warn('Client is already connected');
            }

            else {
                // create a new STOMP client using SockJS
                client = Stomp.over(new SockJS('/topic'));
                client.debug = null;

                // connect to the server-side websockets provider
                var self = this;
                client.connect('mike', 'mike', function () {
                    processConnection(name, color, self);

                }, function (error) {
                    // handle the case where the server drops the client's connection
                    if (error && error.indexOf('Lost connection') > 0) {
                        self.disconnect(true);
                    }
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
         * Sends a player stop request to the server.
         */
        sendStop: function () {
            client.send('/api/user/player/stop', {});
        },

        /**
         * Sends a chat message that the user provided.
         * 
         * @param message {string} The message to send.
         */
        sendChatMessage: function (message) {
            client.send('/api/user/player/chat', {}, JSON.stringify({ message: message }));
        },

        /**
         * Disconnects a previously established connection to the server.
         *
         * @param error {boolean} true if the client disconnected in error, false otherwise.
         */
        disconnect: function (error) {
            if (client !== null) {
                $log.debug('Disconnecting from server');
                scoped(function () {
                    connected = false;
                });

                // let the app know we've disconnected at this point
                dispatchEvent(Events.Disconnected, { error: angular.isDefined(error) && error });
                client.disconnect();
                client = null;
            }

            else {
                $log.warn('Client is already disconnected');
            }
        }
    };
}]);
