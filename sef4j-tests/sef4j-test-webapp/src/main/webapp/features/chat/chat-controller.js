'use strict';

testwebapp.controller('ChatController', function ($scope, $filter, ngTableParams, ChatService) {
	var vm = this;
	
	vm.message = "";
	
	vm.chatIds = [];
	vm.chats = [];
	
	vm.load = function() {
		vm.chatIds = ChatService.getChatIds();
		vm.chats = ChatService.getOpenChats();
	}

	vm.reloadChats = function() {
		vm.load();
	}
	
	vm.openChat = function(chatId) {
		var chat = {
			id: chatId,
			messages: [],
			textEdit: ""
		};
		vm.chats.push(chat);
		vm.reloadChatMessages(chat);
	}
	
	vm.reloadChatMessages = function(chat) {
		ChatService.asyncFindChatMessages(chat.id)
		.then(function(data) {
			chat.messages = data;
		});
	}
	
	vm.sendChatMessage = function(chat) {
		var text = chat.textEdit;
		chat.textEdit = "";
		ChatService.asyncSendChatMessage(chat.id, text)
		.then(function(data) {
			// TODO
			// chat.messages.push(data);
		});
	}
	
	vm.load();
});

