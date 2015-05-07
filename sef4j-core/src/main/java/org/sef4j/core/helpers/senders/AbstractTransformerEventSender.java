package org.sef4j.core.helpers.senders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import org.sef4j.core.api.EventSender;

public abstract class AbstractTransformerEventSender<T,TDest> implements EventSender<T> {

	protected EventSender<TDest> target;
	
	// ------------------------------------------------------------------------

	protected AbstractTransformerEventSender(EventSender<TDest> target) {
		this.target = target;
	}

	// ------------------------------------------------------------------------
	
	@Override
	public void sendEvent(T event) {
		TDest transformedEvent = transformEvent(event);
		target.sendEvent(transformedEvent);
	}

	@Override
	public void sendEvents(Collection<T> events) {
		List<TDest> transformedEvents = new ArrayList<TDest>(events.size());
		for(T event : events) {
			TDest transformedEvent = transformEvent(event);
			transformedEvents.add(transformedEvent);
		}
		target.sendEvents(transformedEvents);
	}

	protected abstract TDest transformEvent(T event);
	
	// ------------------------------------------------------------------------

	public static class FuncTransformEventSender<T,TDest> extends AbstractTransformerEventSender<T,TDest> {
		
		private Function<T,TDest> transformFunction;

		public FuncTransformEventSender(EventSender<TDest> target, Function<T,TDest> transformFunction) {
			super(target);
			this.transformFunction = transformFunction;
		}

		@Override
		protected TDest transformEvent(T event) {
			return transformFunction.apply(event);
		}

		// ------------------------------------------------------------------------
		
		@Override
		public String toString() {
			return "FuncTransformEventSender [transformFunction=" + transformFunction + ", target=" + target + "]";
		}
		
	}
	
}
