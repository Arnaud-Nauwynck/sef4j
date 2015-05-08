package org.sef4j.core.helpers.ioeventchain;

import java.util.function.Predicate;

import org.sef4j.core.api.EventSender;
import org.sef4j.core.api.ioeventchain.DefaultOutputEventChainDefs.FilteredOutputEventChainDef;
import org.sef4j.core.api.ioeventchain.OutputEventChain;
import org.sef4j.core.helpers.senders.AbstractFilterEventSender.PredicateFilterEventSender;
import org.sef4j.core.helpers.senders.DelegateEventSender;

/**
 * OutputEventChain for filtering events by predicate
 * 
 * @param <T>
 */
public class FilteredOutputEventChain<T> extends OutputEventChain<T> {

	private OutputEventChain<T> underlying;

	/**
	 * 
	 * cliSender = client.registerSender()
	 *    |
	 *    \/
	 *    super.innerSender = FilterSender(predicate, sender=proxy)
	 *            |
	 *           \/
	 *          Proxy
	 *                  |
	 *                 \/
	 *         null  or  underlyingSenderHandle
	 *                       |
	 *                      \/
	 *                 underlyingEventChain(..innerEventSender)
	 */
	private PredicateFilterEventSender<T> innerEventSender;
	
	private DelegateEventSender<T> proxyToUnderlyingEventSender;

	private OutputEventChain.SenderHandle<T> underlyingSenderHandle;
	
	// ------------------------------------------------------------------------

	public FilteredOutputEventChain(FilteredOutputEventChainDef def, String displayName,
			OutputEventChain<T> underlying, Predicate<T> predicate) {
		super(def, displayName);
		this.underlying = underlying;
		this.proxyToUnderlyingEventSender = new DelegateEventSender<T>(null);
		this.innerEventSender = new PredicateFilterEventSender<T>(proxyToUnderlyingEventSender, predicate);
	}

	@Override
	public void close() {
		super.close();
		assert underlyingSenderHandle == null;
		
		this.underlying = null;
		this.innerEventSender = null;
		this.proxyToUnderlyingEventSender = null;
	}

	
	// ------------------------------------------------------------------------

	@Override
	protected EventSender<T> getInnerEventSender() {
		return innerEventSender;
	}
	
	@Override
	public boolean isStarted() {
		return underlyingSenderHandle != null && underlying.isStarted();
	}

	@Override
	public void start() {
		if (underlyingSenderHandle == null) {
			underlyingSenderHandle = underlying.registerSender();
			proxyToUnderlyingEventSender.setEventListener(underlyingSenderHandle);
		}
	}

	@Override
	public void stop() {
		if (underlyingSenderHandle != null) {
			proxyToUnderlyingEventSender.setEventListener(null);
			underlying.unregisterSender(underlyingSenderHandle);
			underlyingSenderHandle = null;
		}		
	}	
	
}
