'use strict';

testwebapp.factory('ChatService', function ($http) {

	var handleHttp = function(httpRes) {
		return httpRes.then(function(response) {
			return response.data;
		});
	}
	var asyncFindChatIds = function() {
        return handleHttp($http.get('app/rest/chat'));
    }

	var asyncFindChatMessages = function(chatId) {
		return handleHttp($http.get('app/rest/chat/' + chatId + '/messages'));
	}

	var asyncSendChatMessage = function(chatId, text) {
		return handleHttp($http.post('app/rest/chat/' + chatId + '/sendMessage', text));
	}
	
	var chatIds = [];
	var getChatIds = function() {
		return chatIds;
	}
	var openChats = [];
	
	var getOpenChats = function() {
		return openChats;
	}
	
	// init: async preload
	asyncFindChatIds().then(function(data) {
		// chatIds.push_all(data);
		chatIds = data;
	});
	
	return {
		getChatIds: getChatIds,
		getOpenChats: getOpenChats,
		asyncFindChatIds: asyncFindChatIds,
		asyncFindChatMessages: asyncFindChatMessages,
		asyncSendChatMessage : asyncSendChatMessage,
		
    }
});


