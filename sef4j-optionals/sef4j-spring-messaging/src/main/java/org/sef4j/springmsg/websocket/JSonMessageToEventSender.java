package org.sef4j.springmsg.websocket;

import java.io.IOException;

import org.sef4j.core.api.EventSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonTokenId;

/**
 * <PRE>                             JSonMessageToEventSender
 * WSMessageHandler                   +-----------+                  EventSender<T>
 *       sendEvent(TextMessage json)  |           |   sendEvent(T)
 *                --->                +-----------+      ---->
 *                                        |
 *                                        \/ jsonToObjects()
 *                                     JsonFactory 
 * </PRE>
 * 
 * @param <T>
 */
public class JSonMessageToEventSender<T> { // implements EventSender<TextMessage,T> {

	private static final Logger LOG = LoggerFactory.getLogger(JSonMessageToEventSender.class);
	
	protected EventSender<T> target;

	private JsonFactory jsonFactory;
	private Class<T> messageClass;
	
	private int messageIgnoredCount;
	
	// ------------------------------------------------------------------------
	
	public JSonMessageToEventSender(EventSender<T> target, JsonFactory jsonFactory, Class<T> messageClass) {
		this.target = target;
		this.jsonFactory = jsonFactory;
		this.messageClass = messageClass;
	}
	
	// ------------------------------------------------------------------------

	public void setTarget(EventSender<T> target) {
		this.target = target;
	}

	public void sendEvent(TextMessage jsonEvent) {
		EventSender<T> tmpTarget = target;
		if (tmpTarget == null) {
			messageIgnoredCount++;
			return;
		}

		String json = jsonEvent.getPayload();
		JsonParser parser;
		try {
			parser = jsonFactory.createParser(json);
		} catch (JsonParseException ex) {
			throw new RuntimeException("Failed to parse received message as json: '" + json + "'", ex);
		} catch (IOException ex) {
			throw new RuntimeException("should not occur", ex);
		}
		if (JsonTokenId.ID_START_ARRAY == parser.getCurrentTokenId()) {
			// TODO ... read an array of object
			throw new UnsupportedOperationException("NOT IMPLEMENTED YET ... read json array of " + messageClass);
		} else {
			T event;
			try {
				event = (T) parser.readValueAs(messageClass);
			} catch (IOException ex) {
				throw new RuntimeException("Failed to read mesage as json object " + messageClass + ": '" + json + "'", ex);
			}
			tmpTarget.sendEvent(event);
		}
	}


}
