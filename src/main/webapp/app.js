'use strict';

var app = angular.module('wsApp', [
    'wsApp.graphics.scene'
]);

app.constant('Events', {
    Connected: 'ClientConnected',
    Disconnected: 'ClientDisconnected',
    NewSession: 'ClientNewSession',
    MovePlayer: 'MovePlayer',
    AddEntity: 'AddEntity',
    RemoveEntity: 'RemoveEntity',
    DirChange: 'DirectionChange'
});

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
        isConnected: function () {
            return connected;
        },

        subscribe: function (listener) {
            if (!listener.hasOwnProperty('onClientEvent')) {
                throw new Error('Listener must have a function property called "onClientEvent"!');
            }

            listeners.push(listener);
        },

        connect: function () {
            if (client !== null) {
                $log.warn('Client is already connected');
            }

            else {
                client = Stomp.over(new SockJS('/topic'));
                client.debug = null;
                client.connect('mike', 'mike', function (data) {
                    $log.debug(data);
                    scoped(function () {
                        connected = true;
                    });

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

        sendMove: function (dir) {
            client.send('/api/user/player/move', {}, JSON.stringify({ dir: dir }));
        },

        disconnect: function () {
            if (client !== null) {
                $log.debug('Disconnecting from server');
                scoped(function () {
                    connected = false;
                });

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

app.controller('AppCtrl', ['Client', 'Events', function (Client, Events) {

    var self = this;
    this.statusMessage = 'Not connected to anything';
    this.sceneReady = false;
    this.sceneApi = {};

    this.onClientEvent = function (type, data) {
        switch (type) {
            case Events.Connected:
                self.statusMessage = 'Connected to server, waiting for session...';
                break;
            case Events.NewSession:
                self.statusMessage = 'Opened new client session';
                self.sceneApi.setMap(data);
                break;
            case Events.Disconnected:
                self.statusMessage = 'Not connected to anything';
                break;
            case Events.MovePlayer:
                if (data.valid) {
                    self.sceneApi.movePlayer(data.x, data.y);
                }
                break;
            case Events.AddEntity:
                self.sceneApi.addEntity(data);
                break;
            case Events.RemoveEntity:
                self.sceneApi.removeEntity(data);
                break;
            case Events.DirChange:
                self.sceneApi.creatureDirChange(data.ref, data.dir);
                break;
            default:
                break;
        }
    };

    this.onPlayerMove = function (dir) {
        Client.sendMove(dir);
    };

    this.isSceneReady = function () {
        return this.sceneReady;
    };

    this.onSceneReady = function () {
        self.sceneReady = true;
    };

    this.onConnect = function () {
        Client.connect();
    };

    this.onDisconnect = function () {
        Client.disconnect();
    };

    this.isConnected = function () {
        return Client.isConnected();
    };

    this.init = function () {
        Client.subscribe(this);
    };

    this.init();
}]);