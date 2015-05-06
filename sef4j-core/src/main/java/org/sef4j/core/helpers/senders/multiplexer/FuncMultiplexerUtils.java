package org.sef4j.core.helpers.senders.multiplexer;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.sef4j.core.api.EventSender;
import org.sef4j.core.helpers.senders.multiplexer.AbstractDemultiplexerEventSender.UnwrapInfoPair;

/**
 * helper class for Mulitplexer/Demultiplexer EventSEnder using function delegates
 * 
 * @see also MultiplexerDefaults, for simpler implementation without using functionnal delegates
 */
public final class FuncMultiplexerUtils {
	
	/* private to force all static */
	private FuncMultiplexerUtils() {}
	
	/**
	 * default implementation of AbstractDemultiplexerEventSender using function delegates
	 */
	public static class FuncDemultiplexerEventSender<K,T,TDestEvent> extends AbstractDemultiplexerEventSender<K,T,TDestEvent> {

		protected Function<T,UnwrapInfoPair<K,TDestEvent>> eventUnwrapperFunc;

		protected Function<K,EventSender<TDestEvent>> eventDispatcherFunc;
		
		public FuncDemultiplexerEventSender(
				Function<T, UnwrapInfoPair<K, TDestEvent>> eventUnwrapperFunc,
				Function<K, EventSender<TDestEvent>> eventDispatcherFunc) {
			this.eventUnwrapperFunc = eventUnwrapperFunc;
			this.eventDispatcherFunc = eventDispatcherFunc;
		}

		@Override
		protected UnwrapInfoPair<K, TDestEvent> unwrapEventInfo(T event) {
			UnwrapInfoPair<K, TDestEvent> unwrapInfo = eventUnwrapperFunc.apply(event);
			return unwrapInfo;
		}

		@Override
		protected EventSender<TDestEvent> eventSenderDispatcherFor(K key) {
			return eventDispatcherFunc.apply(key);
		}

	}

	// ------------------------------------------------------------------------

	/**
	 * helper class for MultiplexerEventSender: for wrapping key+event into wrappedEvent 
	 */
	public static class EventToMultiplexedEventFunc<K,T> implements BiFunction<K,T,MultiplexedEvent<K,T>> {

		@SuppressWarnings("rawtypes")
		private static final EventToMultiplexedEventFunc<?,?> INSTANCE = new EventToMultiplexedEventFunc();
		
		@SuppressWarnings("unchecked")
		public static final <K,T> EventToMultiplexedEventFunc<K,T> instance() { return (EventToMultiplexedEventFunc<K,T>) INSTANCE; }

		@Override
		public MultiplexedEvent<K,T> apply(K key, T event) {
			return new MultiplexedEvent<K,T>(key, event);
		}
		
	}

	// ------------------------------------------------------------------------
	
	/**
	 * helper class for DemultiplexerEventSender : for extracting key+unwrappEvent from wrappedEvent
	 */
	public static class MultiplexedEventToEventFunc<K,T> implements Function<MultiplexedEvent<K,T>,UnwrapInfoPair<K,T>> {

		@SuppressWarnings("rawtypes")
		private static final MultiplexedEventToEventFunc<?,?> INSTANCE = new MultiplexedEventToEventFunc();
		
		@SuppressWarnings("unchecked")
		public static final <K,T> MultiplexedEventToEventFunc<K,T> instance() { return (MultiplexedEventToEventFunc<K,T>) INSTANCE; }

		@Override
		public UnwrapInfoPair<K,T> apply(MultiplexedEvent<K, T> event) {
			return new UnwrapInfoPair<K,T>(event.getKey(), event.getWrappedEvent());
		}
		
	}

	// ------------------------------------------------------------------------
	
	/**
	 * helper class for DemultiplexerEventSender: for selecting an EventDispatcher in a map to dispatch for a given key
	 */
	public static class EventSenderDispatcherMap<K,T> {
		
		protected Map<K,EventSender<T>> map;
		protected EventSender<T> defaultEventSender;
		protected final Function<K,EventSender<T>> eventDispatcherFunc;
		
		public EventSenderDispatcherMap(Map<K,EventSender<T>> map, EventSender<T> defaultEventSender) {
			this.map = map;
			this.defaultEventSender = defaultEventSender;
			this.eventDispatcherFunc = key -> getEventSender(key);
		}
		
		public Function<K, EventSender<T>> getEventDispatcherFunc() {
			return eventDispatcherFunc;
		}

		protected EventSender<T> getEventSender(K key) {
			EventSender<T> res = map.get(key);
			if (res == null) {
				res = defaultEventSender;
			}
			return res;
		}

	}
	
	// ------------------------------------------------------------------------
	
	public static class FuncMultiplexerEventSender<K,TSrcEvent,TDestEvent> 
		extends AbstractMultiplexerEventSender<K,TSrcEvent,TDestEvent> {

		protected BiFunction<K,TSrcEvent,TDestEvent> eventWrapperFunc;

		public FuncMultiplexerEventSender(
				EventSender<TDestEvent> target,
				BiFunction<K, TSrcEvent, TDestEvent> eventWrapperFunc) {
			super(target);
			this.eventWrapperFunc = eventWrapperFunc;
		}

		@Override
		protected TDestEvent wrapEvent(K key, TSrcEvent event) {
			return eventWrapperFunc.apply(key, event);
		}

	}
	
}
