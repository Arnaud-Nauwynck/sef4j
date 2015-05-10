package org.sef4j.core.api.ioeventchain;

import java.io.Closeable;

import org.sef4j.core.api.EventSender;
import org.sef4j.core.helpers.senders.DefaultEventProvider;
import org.sef4j.core.util.Handle;
import org.sef4j.core.util.factorydef.AbstractStartableDefObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * chain of EventSender (EventSender->EventSender->EventSender)
 * 
 * instance of this class are managed by ObjectByDefRepository 
 * and are unique for the given corresponding InputEventChainDef
 * 
 * to obtain output events from this chain;
 * <code>
 * EventSender<T> myEventCallback = ...
 * InputEventChain<T> inputChain = ... // repository.register(def);
 * 
 * ListenerHandle<T> subscr = inputChain.registerEventListener(myEventCallback);
 * ...
 * ... // receive events: calls myEventCallback.sendEvents(..)
 * ...
 * inputChain.unregisterEventListener(subscr);
 * </code>
 */
public abstract class InputEventChain<T> extends AbstractStartableDefObject<InputEventChainDef> {

	private static final Logger LOG = LoggerFactory.getLogger(InputEventChain.class);
	
	protected DefaultEventProvider<T> innerEventProvider = new DefaultEventProvider<T>();
	

	// ------------------------------------------------------------------------

	public InputEventChain(InputEventChainDef def, String displayName) {
		super(def, displayName);
	}

	// ------------------------------------------------------------------------
	
	public ListenerHandle<T> registerEventListener(EventSender<T> listener) {
		innerEventProvider.addEventListener(listener);
		Handle handle = registerStart();
		return new ListenerHandle<T>(this, handle, listener);
	}

	public void unregisterEventListener(ListenerHandle<T> handle) {
		innerEventProvider.removeEventListener(handle.listener);
		unregisterStop(handle.handle);		
	}
	
	// TODO use only in sub-classes ???
	public EventSender<T> getInnerEventSender() {
		return innerEventProvider;
	}
	
	// ------------------------------------------------------------------------
	
	public static class ListenerHandle<T> implements Closeable {

		private InputEventChain<T> target;
		private Handle handle;
		private EventSender<T> listener;
		
		private ListenerHandle(InputEventChain<T> target, Handle handle, EventSender<T> listener) {
			this.target = target;
			this.handle = handle;
			this.listener = listener;
		}
		
		@Override
		protected void finalize() throws Throwable {
			if (target != null) {
				LOG.warn("detected unreleased resource ... force close()!");
				try {
					close();
				} catch(Exception ex) {
					LOG.error("Failed ... ignore, no rethrow!", ex);
				}
			}
			super.finalize();
		}

		@Override
		public void close() {
			if (target != null && handle != null) {
				target.unregisterEventListener(this);
			}
			this.handle = null;
		}

	}

}
