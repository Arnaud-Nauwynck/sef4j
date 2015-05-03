'use strict';
angular.module('testwebapp')
.service("StatsAsyncService", function($q, $timeout) {
	var service = {};

	service.RECONNECT_TIMEOUT = 30000;
	service.SOCKET_URL = "/pendingCount";
	service.PENDINGCOUNT_TOPIC = "/topic/pendingCount";
	service.PENDINGCOUNT_BROKER = "/app/async/pendingCount";

	var listener = $q.defer();

	var socket = {
			client: null,
			stomp: null
	};

	var subscription = null;


	service.receive = function() {
		return listener.promise;
	};

	service.sendPendingCountRequest = function(request) {
		var id = Math.floor(Math.random() * 1000000);
		var requestText = JSON.stringify(request);
		socket.stomp.send(service.PENDINGCOUNT_BROKER, requestText);
	};

	service.reconnect = function() {
		$timeout(function() {
			service.initialize();
		}, this.RECONNECT_TIMEOUT);
	};


	service.pendingCountListeners = [];

	service.addListener = function(listener) {
		this.pendingCountListeners.push(listener);
	};

	service.removeListener = function(listener) {
		var index = -1;
		for(var i = 0; i < this.pendingCountListeners.length; i++) {
			if (this.pendingCountListeners[i] === listener) {
				index = i;
				break;
			}
		}
		this.pendingCountListeners.slice(i, 0);
	};

	service.fireEventListener = function(event) {
		this.pendingCountListeners.forEach(function(listener) {
			listener(event);
		});
	}


	service.onEventData = function(event) {
		listener.notify(event); // fire using angular promise.notify
		this.fireEventListener(event);
	};

	service.subscribe = function() {
		this.subscription  = socket.stomp.subscribe(this.PENDINGCOUNT_TOPIC, function(data) {
			var event = JSON.parse(data.body);
			service.onEventData(event); // this is undefined?!
		});
	};

	service.unsubscribe = function() {
		if (this.subscription != null) {
			this.subscription.unsubscribe();
			this.subscription = null;
		}
	};


	service.initialize = function() {
		socket.client = new SockJS(this.SOCKET_URL);
		socket.stomp = Stomp.over(socket.client);
		socket.stomp.connect({}, this.subscribe.bind(this));
		socket.stomp.onclose = this.reconnect.bind(this);
	};

	
	service.initialize();
	return service;
});