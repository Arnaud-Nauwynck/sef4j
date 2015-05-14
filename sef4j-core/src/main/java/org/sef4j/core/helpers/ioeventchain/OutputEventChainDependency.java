package org.sef4j.core.helpers.ioeventchain;

import java.util.Collection;

import org.sef4j.core.api.EventSender;
import org.sef4j.core.api.ioeventchain.OutputEventChain;
import org.sef4j.core.helpers.senders.DelegateEventSender;

/**
 * helper class for using OutputEventChain dependency 
 * (= delegate OutputEventChain + handle )
 *
 * @param <T>
 */
public class OutputEventChainDependency<T> {

	private OutputEventChain<T> delegate;

	private OutputEventChain.SenderHandle<T> senderHandle;
	
	private DelegateEventSender<T> innerEventSender = new DelegateEventSender<T>(null);
	
	// ------------------------------------------------------------------------
	
	public OutputEventChainDependency(OutputEventChain<T> delegate) {
		this.delegate = delegate;
	}
	
	// ------------------------------------------------------------------------
	
	public EventSender<T> getInnerEventSender() {
		return innerEventSender;
	}

	public boolean isStarted() {
		return senderHandle != null && delegate.isStarted();
	}
	
	public void start() {
		if (senderHandle == null) {
			senderHandle = delegate.registerSender();
			innerEventSender.setEventListener(senderHandle);
		}
	}
	
	public void stop() {
		if (senderHandle != null) {
			delegate.unregisterSender(senderHandle);
			senderHandle = null;
			innerEventSender.setEventListener(null);
		}
	}
	
	// ------------------------------------------------------------------------
	
	public static <T> boolean areAllStarted(Collection<OutputEventChainDependency<T>> ls) {
		boolean res = true;
		for(OutputEventChainDependency<T> e : ls) {
			if (!e.isStarted()) {
				res = false;
				break;
			}
		}
		return res;
	}

	public static <T> void startAll(Collection<OutputEventChainDependency<T>> ls) {
		for(OutputEventChainDependency<T> e : ls) {
			e.start();
		}
	}

	public static <T> void stopAll(Collection<OutputEventChainDependency<T>> ls) {
		for(OutputEventChainDependency<T> e : ls) {
			e.stop();
		}
	}

}
