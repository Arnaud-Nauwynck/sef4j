package org.sef4j.springmsg.websocket;

import java.util.List;

import org.sef4j.core.api.EventSender;
import org.sef4j.core.api.session.InOutEventsClientSession;
import org.sef4j.core.api.session.InOutEventsClientSessionManager;
import org.sef4j.core.api.session.SubscriptionCommandDTO;
import org.sef4j.core.api.session.SubscriptionResponseDTO;
import org.sef4j.core.util.CopyOnWriteUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import com.fasterxml.jackson.core.JsonFactory;
import com.google.common.collect.ImmutableMap;

/**
 * 
 */
public class ClientSessionTransportWebSocketHandler extends AbstractWebSocketHandler {

	private static final Logger LOG = LoggerFactory.getLogger(ClientSessionTransportWebSocketHandler.class);
	
	public static final String ATTR_associatedClientSessionId = "associatedClientSessionId";
	
	protected InOutEventsClientSessionManager clientSessionManager;
	
	private ImmutableMap<String,WebSocketEntry> webSocketEntries = ImmutableMap.of();

	private Object lock = new Object();

	private JsonFactory jsonFactory = new JsonFactory();
	
	// ------------------------------------------------------------------------

	public ClientSessionTransportWebSocketHandler(InOutEventsClientSessionManager clientSessionManager) {
		this.clientSessionManager = clientSessionManager;
	}

	// ------------------------------------------------------------------------

	/** called from WebSocketHandler */
	public void onWebSocketSessionCreated(WebSocketSession wsSession) {
		String wsId = wsSession.getId();
		String associatedClientSessionId = (String) wsSession.getAttributes().get(ATTR_associatedClientSessionId);
		LOG.info("onWebSocketSessionCreated " + wsId + ((associatedClientSessionId != null? " associatedClientSessionId:" + associatedClientSessionId : "")));
		
		InOutEventsClientSession clientSession = (associatedClientSessionId != null)?
				getClientSessionOrThrow(associatedClientSessionId) : null;
		JsonWSMessageEventSender jsonMessageSender = new JsonWSMessageEventSender(wsSession, jsonFactory);
		JSonMessageToEventSender<Object> jsonMessageReceiver = new JSonMessageToEventSender<Object>(null, jsonFactory, 
				Object.class // TODO ???!!! class for json->object converter
				);
		
		WebSocketEntry entry = new WebSocketEntry(wsSession, clientSession, jsonMessageSender, jsonMessageReceiver);
		synchronized (lock) {
			webSocketEntries = CopyOnWriteUtils.newWithPut(webSocketEntries, wsId, entry);
			if (clientSession != null) {
				doAttachWebSocketToClientSession(entry, clientSession);
			}
		}
	}

	/** called from WebSocketHandler */
	public void onWebSocketSessionDeleted(WebSocketSession wsSession) {
		String wsId = wsSession.getId();
		LOG.info("onWebSocketSessionDeleted " + wsId);
		WebSocketEntry entry = webSocketEntries.get(wsId);
		if (entry == null) {
			LOG.warn("webSocket entry '" + wsId + "' not found ... ignore!");
			return;
		}
		// TODO 
		if (entry.clientSession != null) {
			doDetachWebSocketFromClientSession(entry, entry.clientSession);
		}
		synchronized (lock) {
			webSocketEntries = CopyOnWriteUtils.newWithRemove(webSocketEntries, wsId);
		}
	}

	public void attachWebSocketToClientSession(WebSocketSession wsSession, String clientSessionId) {
		String wsId = wsSession.getId();
		LOG.info("attachWebSocketToClientSession " + wsId + " " + clientSessionId);
		WebSocketEntry entry = webSocketEntries.get(wsId);
		InOutEventsClientSession clientSession = getClientSessionOrThrow(clientSessionId);

		doAttachWebSocketToClientSession(entry, clientSession);
	}

	public void detachWebSocketFromClientSession(WebSocketSession wsSession, String clientSessionId) {
		String wsId = wsSession.getId();
		LOG.info("detachWebSocketToClientSession " + wsId + " " + clientSessionId);
		WebSocketEntry entry = webSocketEntries.get(wsId);
		InOutEventsClientSession clientSession = getClientSessionOrThrow(clientSessionId);

		doDetachWebSocketFromClientSession(entry, clientSession);
	}
		
	public void redispatchReceiveMessageToClientSession(WebSocketSession wsSession, TextMessage message) {
		String wsId = wsSession.getId();
		WebSocketEntry entry = webSocketEntries.get(wsId);
		if (entry == null) {
			LOG.warn("webSocket entry '" + wsId + "' not found ... ignore message!");
			return;
		}
		entry.messageReceiver.sendEvent(message);
	}

    public List<SubscriptionResponseDTO> handleSubscriptionCommands(WebSocketSession wsSession, List<SubscriptionCommandDTO> commands) {
		String wsId = wsSession.getId();
		WebSocketEntry entry = webSocketEntries.get(wsId);
		if (entry == null) {
			LOG.warn("webSocket entry '" + wsId + "' not found ... ignore message!");
			throw new IllegalStateException("webSocket entry not found for wsId: '" + wsId + "'");
		}
		List<SubscriptionResponseDTO> res = entry.clientSession.getInputEventChainSubscriptions().handleSubscriptionCommands(commands);
		return res;
    }
    
	// internal
	// ------------------------------------------------------------------------
	
	private void doAttachWebSocketToClientSession(WebSocketEntry entry, InOutEventsClientSession clientSession) {
		if (entry.clientSession == clientSession) {
			return; // do nothing (should not occur however)
		}
		if (entry.clientSession != null) {
			throw new IllegalStateException("WebSocket already associated with another clientSession");
		}
		entry.clientSession = clientSession;
		
		// attach event receiver: clientSession <-- WebSocket
		EventSender<Object> clientSessionMessageReceiver = clientSession.getSessionTransportService().getSessionTransportsEventReceiver();
		entry.messageReceiver.setTarget(clientSessionMessageReceiver);
		
		// attach event sender: clientSession --> WebSocket
		clientSession.getSessionTransportService().addSessionTransportsEventListener(entry.webSocketEventSender);
	}
	
	private void doDetachWebSocketFromClientSession(WebSocketEntry entry, InOutEventsClientSession clientSession) {
		if (entry.clientSession == null) {
			return; // do nothing (should not occur however)
		}
		entry.clientSession = null;
		
		// deattach event receiver: clientSession <-- WebSocket
		entry.messageReceiver.setTarget(null);
		
		// detach event sender: clientSession --> WebSocket
		clientSession.getSessionTransportService().removeSessionTransportsEventListener(entry.webSocketEventSender);
	}
	

	private InOutEventsClientSession getClientSessionOrThrow(String associatedClientSessionId) {
		InOutEventsClientSession clientSession;
		clientSession = clientSessionManager.getClientSessionOrNull(associatedClientSessionId);
		if (clientSession == null) {
			throw new IllegalArgumentException("clientSessionId '" + associatedClientSessionId + "' not found");
		}
		return clientSession;
	}

	// ------------------------------------------------------------------------
	
	private static class WebSocketEntry {
		WebSocketSession wsSession;
		InOutEventsClientSession clientSession;
		EventSender<Object> webSocketEventSender;
		JSonMessageToEventSender<Object> messageReceiver;
		
		public WebSocketEntry(WebSocketSession wsSession, 
				InOutEventsClientSession clientSession, 
				EventSender<Object> webSocketEventSender,
				JSonMessageToEventSender<Object> messageReceiver) {
			this.wsSession = wsSession;
			this.clientSession = clientSession;
			this.webSocketEventSender = webSocketEventSender;
			this.messageReceiver = messageReceiver;
		}

		@Override
		public String toString() {
			return "WebSocketEntry [wsSession=" + ((wsSession != null)? wsSession.getId() : "")
					+ ", clientSession=" + ((clientSession != null)? clientSession.getId() : "null") 
					+ "]";
		}
	
		
	}
	

}
