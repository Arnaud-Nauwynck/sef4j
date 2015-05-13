package org.sef4j.core.api.ioeventchain;

import java.io.Closeable;
import java.util.Collection;

import org.sef4j.core.api.EventSender;
import org.sef4j.core.util.Handle;
import org.sef4j.core.util.factorydef.AbstractSharedStartableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * chain of EventProvider (EventProvider<-EventProvider<-EventProvider)
 * 
 * instance of this class are managed by ObjectByDefRepository 
 * and are unique for the given corresponding InputEventChainDef
 * 
 * to output events to this chain;
 * <code>
 * OutputEventChain<T> outputChain = ... // repository.register(def);
 * 
 * OutputEventChainPublication<T> subscr = outputChain.addOutputSender();
 * ...
 * ... // send events:
 *  subscr.sendEvents(..)
 *  
 * ...
 * subscr.close();
 * </code>
 * 
 */
public abstract class OutputEventChain<T> extends AbstractSharedStartableObject<OutputEventChainDef> {

	private static final Logger LOG = LoggerFactory.getLogger(OutputEventChain.class);

	// ------------------------------------------------------------------------

	public OutputEventChain(String displayName) {
		super(displayName);
	}

	// ------------------------------------------------------------------------

	protected abstract EventSender<T> getInnerEventSender();
	
	public SenderHandle<T> registerSender() {
		Handle handle = registerStart();
		return new SenderHandle<T>(this, handle);
	}

	public void unregisterSender(SenderHandle<T> handle) {
		handle.close();
	}
	
	// ------------------------------------------------------------------------
	
	public static class SenderHandle<T> implements EventSender<T>, Closeable {
		
		private OutputEventChain<T> target;
		private Handle handle;
		
		private SenderHandle(OutputEventChain<T> target, Handle handle) {
			this.target = target;
			this.handle = handle;
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
			if (target != null) {
				target.unregisterStop(handle);
				this.target = null;
				this.handle = null;
			}
		}

		@Override
		public void sendEvent(T event) {
			if (target == null) return;
			EventSender<T> innerSender = target.getInnerEventSender();
			innerSender.sendEvent(event);
		}

		@Override
		public void sendEvents(Collection<T> events) {
			if (target == null) return;
			EventSender<T> innerSender = target.getInnerEventSender();
			innerSender.sendEvents(events);
		}
		
	}

}
