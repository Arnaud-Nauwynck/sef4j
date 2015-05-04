package org.sef4j.testwebapp.web;

import org.sef4j.testwebapp.service.PerfStatsSubscriptionSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

@Component
public class PerfStatsWebSocketHandler implements WebSocketHandler {

	@Autowired 
	protected PerfStatsSubscriptionSessionManager sessionMgr;
	
	// ------------------------------------------------------------------------

	public PerfStatsWebSocketHandler() {
	}

	// ------------------------------------------------------------------------
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		sessionMgr.onWebSocketSessionCreated(session);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
		sessionMgr.onWebSocketSessionDeleted(session);
	}

	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
		// do nothing??
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		// do nothing ??
	}

	@Override
	public boolean supportsPartialMessages() {
		return false;
	}

}
