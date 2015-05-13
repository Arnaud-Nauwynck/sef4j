package org.sef4j.core.helpers.ioeventchain;

import java.util.Collection;

import org.sef4j.core.api.EventSender;
import org.sef4j.core.api.ioeventchain.InputEventChain;

/**
 * helper class for using InputEventChain dependency 
 * (= delegate InputEventChain + listener + listenerHandle )
 *
 * @param <T>
 */
public class InputEventChainDependency<T> {

	private InputEventChain<T> delegate;

	private InputEventChain.ListenerHandle<T> listenerHandle;

	private EventSender<T> delegateListener;
	
	// ------------------------------------------------------------------------
	
	public InputEventChainDependency(InputEventChain<T> delegate, EventSender<T> delegateListener) {
		this.delegate = delegate;
		this.delegateListener = delegateListener;
	}
	
	// ------------------------------------------------------------------------
	
	public boolean isStarted() {
		return listenerHandle != null && delegate.isStarted();
	}
	
	public void startListener() {
		if (listenerHandle == null) {
			listenerHandle = delegate.registerEventListener(delegateListener);
		}
	}
	
	public void stopListener() {
		if (listenerHandle != null) {
			delegate.unregisterEventListener(listenerHandle);
			listenerHandle = null;
		}
	}
	
	// ------------------------------------------------------------------------
	
	public static <T> boolean areAllStarted(Collection<InputEventChainDependency<T>> ls) {
		boolean res = true;
		for(InputEventChainDependency<T> e : ls) {
			if (!e.isStarted()) {
				res = false;
				break;
			}
		}
		return res;
	}

	public static <T> void startListenerAll(Collection<InputEventChainDependency<T>> ls) {
		for(InputEventChainDependency<T> e : ls) {
			e.startListener();
		}
	}

	public static <T> void stopListenerAll(Collection<InputEventChainDependency<T>> ls) {
		for(InputEventChainDependency<T> e : ls) {
			e.stopListener();
		}
	}

}
