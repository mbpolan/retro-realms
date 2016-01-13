'use strict';

var app = angular.module('wsApp', []);

app.constant('Events', {
    Connected: 'ClientConnected',
    Disconnected: 'ClientDisconnected',
    NewSession: 'ClientNewSession'
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
                client.connect({}, function () {
                    $log.debug('Connected to websocket server');
                    scoped(function () {
                        connected = true;
                    });

                    dispatchEvent(Events.Connected);

                    client.send('/api/user', {}, JSON.stringify({
                        name: 'Mike'
                    }));

                    client.subscribe('/topic/user', function (data) {
                        sessionId = JSON.parse(data.body).sessionId;
                        dispatchEvent(Events.NewSession, {
                            id: sessionId
                        });
                    });
                });
            }
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
    this.title = 'My App';
    this.statusMessage = 'Not connected to anything';

    this.onClientEvent = function (type, data) {
        switch (type) {
            case Events.Connected:
                self.statusMessage = 'Connected to server, waiting for session...';
                break;
            case Events.NewSession:
                self.statusMessage = 'Opened new client session';
                break;
            case Events.Disconnected:
                self.statusMessage = 'Not connected to anything';
                break;
            default:
                break;
        }
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