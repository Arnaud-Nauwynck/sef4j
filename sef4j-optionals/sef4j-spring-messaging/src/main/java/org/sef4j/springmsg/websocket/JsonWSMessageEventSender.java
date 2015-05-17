package org.sef4j.springmsg.websocket;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;

import org.sef4j.core.api.EventSender;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

/**
 * EventSEnder adapter for sending spring WebSocketMessage Json message to WebSocketSession
 * <PRE>
 *                    JsonWSMessageEventSender                  WebSocketSession
 * sendEvent(Object)  +---------------+       sendMessage(json)  +-------+
 *  --->              |               |      ------->            |       |
 *                    +---------------+                          +-------+
 *                           |
 *                          \/ json=eventToMessage(cObject)
 *                     JSonFactory
 *                        +-------+
 *                        |       |
 *                        +-------+
 * </PRE>
 */
public class JsonWSMessageEventSender implements EventSender<Object> {

	private WebSocketSession wsSession;
	
	private JsonFactory jsonFactory;
	
	// ------------------------------------------------------------------------

	public JsonWSMessageEventSender(WebSocketSession wsSession, JsonFactory jsonFactory) {
		this.wsSession = wsSession;
		this.jsonFactory = jsonFactory;
	}
	
	// ------------------------------------------------------------------------
	
	@Override
	public void sendEvent(Object event) {
		WebSocketMessage<?> message = eventToMessage(event);
		try {
			wsSession.sendMessage(message);
		} catch (IOException ex) {
			throw new RuntimeException("Failed to send message for event", ex);
		}
	}

	@Override
	public void sendEvents(Collection<Object> events) {
		WebSocketMessage<?> message = eventsToMessage(events);
		try {
			wsSession.sendMessage(message);
		} catch (IOException ex) {
			throw new RuntimeException("Failed to send message for event", ex);
		}
	}

	protected WebSocketMessage<?> eventToMessage(Object event) {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		JsonGenerator jsonGenerator = createJsonGenerator(bout);
		try {
			jsonGenerator.writeObject(event);
		} catch (IOException ex) {
			throw new RuntimeException("should not occur", ex);
		}
		WebSocketMessage<?> message = new TextMessage(bout.toByteArray());
		return message;
	}

	protected WebSocketMessage<?> eventsToMessage(Collection<Object> events) {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		JsonGenerator jsonGenerator = createJsonGenerator(bout);
		for(Object event : events) {
			try {
				jsonGenerator.writeObject(event);
			} catch (IOException ex) {
				throw new RuntimeException("should not occur", ex);
			}
		}
		WebSocketMessage<?> message = new TextMessage(bout.toByteArray());
		return message;
	}

	protected JsonGenerator createJsonGenerator(ByteArrayOutputStream bout) {
		try {
			JsonGenerator res = jsonFactory.createGenerator(bout);
			return res;
		} catch (IOException ex) {
			throw new RuntimeException("should not occur", ex);
		}
	}

	// ------------------------------------------------------------------------

	@Override
	public String toString() {
		return "JsonWSMessageEventSender [wsSession=" + wsSession + "]";
	}
	
}
