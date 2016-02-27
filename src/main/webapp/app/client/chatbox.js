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
        templateUrl: 'app/client/chatbox.html',
        link: function (scope, el, attrs, ctrl) {
            var textArea = el.find('textarea')[0];
            var input = el.find('input')[0];

            ctrl.showLatestMessages = function () {
                textArea.scrollTop = textArea.scrollHeight;
            };
            
            ctrl.autoFocus = function () {
                input.focus();
            };
        }
    };
}]);

/**
 * Controller that drives the chat interface.
 */
module.controller('ChatBoxCtrl', ['$log', '$timeout', 'Client', function ($log, $timeout, Client) {

    var self = this;
    this.api = this.api || {};
    this.chatHistory = '';
    this.userInput = null;

    /**
     * Handler invoked when the user wishes to send their chat message.
     */
    this.onSend = function () {
        if (self.isMessageValid()) {
            Client.sendChatMessage(self.userInput.trim());
            self.userInput = null;
        }
    };

    /**
     * Determines if the current chat message can be sent.
     * 
     * @returns {boolean} true if the current message cannot be sent, false otherwise.
     */
    this.isMessageValid = function () {
        return self.userInput !== null && self.userInput.trim().length > 0;
    };

    /**
     * Initializes the controller.
     */
    this.init = function () {
        self.onInit && self.onInit();
    };

    /**
     * Sets the input focus on the text input for chat messages.
     */
    this.api.focusElements = function () {
        self.autoFocus();
    };

    /**
     * Appends a player message to the current chat history.
     *
     * @param name {string} The name of the player who sent the message.
     * @param text {string} The content of the message.
     */
    this.api.addMessage = function (name, text) {
        self.chatHistory += '\n' + name + ': ' + text;

        $timeout(function () {
            self.showLatestMessages();
        });
    };

    this.init();
}]);