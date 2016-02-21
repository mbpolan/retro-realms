'use strict';

var module = angular.module('wsApp.client.chatBox', []);

/**
 * Directive that creates a panel for chat messages and input.
 */
module.directive('chatBox', [function () {

    return {
        restrict: 'E',
        replace: true,
        scope: {
            api: '=',
            onInit: '&'
        },
        controller: 'ChatBoxCtrl',
        controllerAs: 'ctrl',
        bindToController: true,
        templateUrl: 'client/chatbox.html'
    };
}]);

/**
 * Controller that drives the chat interface.
 */
module.controller('ChatBoxCtrl', ['$log', 'Client', function ($log, Client) {

    var self = this;
    this.api = this.api || {};
    this.chatHistory = '';
    this.userInput = null;

    this.onSend = function () {
        Client.sendChatMessage(self.userInput);
        self.userInput = null;
    };

    this.init = function () {
        self.onInit && self.onInit();
    };

    this.api.addMessage = function (name, text) {
        self.chatHistory += '\n' + name + ': ' + text;
    };

    this.init();
}]);