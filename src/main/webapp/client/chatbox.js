'use strict';

var module = angular.module('wsApp.client.chatBox', []);

/**
 * Directive that creates a panel for chat messages and input.
 */
module.directive('chatBox', [function () {

    return {
        restrict: 'E',
        replace: true,
        scope: {},
        controller: 'ChatBoxCtrl',
        controllerAs: 'ctrl',
        bindToController: true,
        templateUrl: 'client/chatbox.html'
    };
}]);

/**
 * Controller that drives the chat interface.
 */
module.controller('ChatBoxCtrl', ['$log', function ($log) {
    
    this.userInput = null;
}]);