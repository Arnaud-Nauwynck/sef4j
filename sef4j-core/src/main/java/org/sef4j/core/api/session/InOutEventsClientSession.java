package org.sef4j.core.api.session;

import java.util.Collection;

import org.sef4j.core.api.EventProvider;
import org.sef4j.core.api.EventSender;
import org.sef4j.core.helpers.senders.DefaultEventProvider;

/**
 * Client Session support class for input-output events 
 * between hosted Application listener/sender <--- to/from---> SessionTransport listener/sender
 */
public class InOutEventsClientSession {

	public String sessionDisplayName;
	
	/**
	 * event redirector from hosted application provider to client session transport(s)
	 * Notice there should be only 1 session listener in general 
	 * ... otherwise client will receive multiple occurrences of each event, one per transport
	 * 
	 * <PRE>
	 *                                          addSessionTransportEventListener
	 *                                        <---
	 * 
	 * (appSender1)                                  clientSession Transport (example: JMS, WebSocket, ..)
	 *    sendEvent(e1)  +--------------+  sendEvent(e1)   
	 *      ----->       | appToSession |       ------->  +---> transport1
	 *                   |              |                  \--> transport2
 	 * (appSender2)      +--------------+                    ...
	 *    sendEvent(e2)                    sendEvent(e2)
	 *      ----->                               ------>  +---> transport1
	 *                                                     \--> transport2
	 *                                                       ...
	 *                                          removeSessionTransportEventListener()
	 *                                        <---
	 * </PRE>
	 * 
	 * Notice that appSender(s) should generally be wrapped with a MultiplexerEventSender
	 */
	protected DefaultEventProvider<Object> appToSessionTransportsEventSender = new DefaultEventProvider<Object>();

	/**
	 * event redirector from client session transport(s) to hosted application event listener
	 * 
	 * <PRE>
	 * addAppEventListener(appListener1)
	 *    --->
	 * addAppEventListener(appListener2)
	 *    --->
	 *                                                 clientSession Transport (example: JMS, WebSocket, ..)
	 *                 sendEvent(e1) +--------------+  sendEvent(e1)
	 * appListener1 <---+ <-----     | appToSession |    <------
	 * appListener2 <--/             |              |
 	 *                               +--------------+  
	 *                sendEvent(e2)                    sendEvent(e2)
	 * appListener1 <---+ <-----                         <------
	 * appListener2 <--/  
	 *                
	 * removeAppEventListener(appListener1)
	 *    --->
	 * </PRE>
	 * 
	 */
	protected DefaultEventProvider<Object> sessionTransportsToAppListenersEventSender = new DefaultEventProvider<Object>();

	/**
	 * <PRE>  inner                inner 
	 *      AppService +--------+  SessionTransportService
	 *  --->   +----+  | this   |  +----+    <---
	 *  --->   |    |--|        |--|    |    <---
	 *  --->   +----+  |        |  +----+    <---
	 *           /\    +--------+    /\
	 *            |                   |
	 *         +----+               +----+
	 * --->    |    |               |    |  <---
	 *         +----+               +----+
	 *      InputEventChain         OutputEventChain
	 *      Subscriptions           Publications
	 * </PRE>
	 */
	private SessionTransportService innerSessionTransportService = new SessionTransportService();
	private AppService innerAppService = new AppService();

	private ClientSessionInputEventChainSubscriptions inputEventChainSubscriptions = 
			new ClientSessionInputEventChainSubscriptions(this);
	
	// ------------------------------------------------------------------------

	public InOutEventsClientSession(String sessionDisplayName) {
		this.sessionDisplayName = sessionDisplayName;
	}

	// ------------------------------------------------------------------------

	public SessionTransportService getSessionTransportService() {
		return innerSessionTransportService ;
	}
	
	public AppService getAppService() {
		return innerAppService;
	}

	public ClientSessionInputEventChainSubscriptions getInputEventChainSubscriptions() {
		return inputEventChainSubscriptions;
	}

	
	// ------------------------------------------------------------------------
	
	/**
	 * inner api service for SessionTransports side
	 * <PRE>
	 *             addSessionTransportListener()    ...example: on WebSocket connect
	 *           <---
	 * +----+      removeSessionTransportListener() ...example: on WebSocket disconnect
	 * |    |    <---
	 * +----+       
	 *             sendSessionTransportEvent()      ...example: on WebSocket receive event  
	 *           <--
	 * </PRE>
	 */
	public class SessionTransportService {
		
		public EventProvider<Object> getSessionTransportsEventProvider() {
			return appToSessionTransportsEventSender;
		}
	
		/** helper for <code>getSessionTransportsEventProvider().addEventListener()</code> */
		public void addSessionTransportsEventListener(EventSender<Object> listener) {
			appToSessionTransportsEventSender.addEventListener(listener);
		}
		/** helper for <code>getSessionTransportsEventProvider().removeEventListener()</code> */
		public void removeSessionTransportsEventListener(EventSender<Object> listener) {
			appToSessionTransportsEventSender.removeEventListener(listener);
		}

		public EventSender<Object> getSessionTransportsEventSender() {
			return sessionTransportsToAppListenersEventSender;
		}

		/** helper for <code>getSessionTransportsEventSender().sendEvent()</code> */
		public void sendSessionTransportsEvent(Object event) {
			sessionTransportsToAppListenersEventSender.sendEvent(event);
		}
		/** helper for <code>getSessionTransportsEventSender().sendEvents()</code> */
		public void sendSessionTransportsEvents(Collection<Object> events) {
			sessionTransportsToAppListenersEventSender.sendEvents(events);
		}

	}
	
	/**
	 * inner api service for appListener side
	 * <PRE>
	 * addAppEventListener()
	 *    --->                     +------+
	 * removeAppEventListener()    |      |
	 *    --->                     +------+
	 *  
	 *   sendEvent()
	 *    --->
	 * </PRE>
	 */
	public class AppService {
		public EventProvider<Object> getAppEventProvider() {
			return sessionTransportsToAppListenersEventSender;
		}
	
		/** helper for <code>getSessionTransportsEventProvider().addEventListener()</code> */
		public void addAppEventListener(EventSender<Object> listener) {
			sessionTransportsToAppListenersEventSender.addEventListener(listener);
		}
		/** helper for <code>getSessionTransportsEventProvider().removeEventListener()</code> */
		public void removeAppEventListener(EventSender<Object> listener) {
			sessionTransportsToAppListenersEventSender.removeEventListener(listener);
		}

		public EventSender<Object> getAppEventSender() {
			return appToSessionTransportsEventSender;
		}

		/** helper for <code>sessionTransportsToAppListenersEventSender.sendEvent()</code> */
		public void sendAppEvent(Object event) {
			appToSessionTransportsEventSender.sendEvent(event);
		}
		/** helper for <code>getSessionTransportsEventSender().sendEvents()</code> */
		public void sendAppEvents(Collection<Object> events) {
			appToSessionTransportsEventSender.sendEvents(events);
		}
		
	}
	
	// ------------------------------------------------------------------------
	
	public void close() {
		// TODO ...
	}
	
}
