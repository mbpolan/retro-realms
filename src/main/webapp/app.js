'use strict';

var app = angular.module('wsApp', []);

app.factory('Client', ['$log', function($log) {

    var client = null;

    return {
        connect: function () {
            if (client !== null) {
                $log.warn('Client is already connected');
            }

            else {
                client = Stomp.over(new SockJS('/topic'));
                client.connect({}, function () {
                    client.subscribe('/topic/user', function (greeting) {

                    });
                });
            }
        },

        disconnect: function () {
            if (client !== null) {
                client.disconnect();
                client = null;
            }

            else {
                $log.warn('Client is already disconnected');
            }
        }
    };
}]);

app.controller('AppCtrl', [function() {

    this.title = 'My App';

}]);