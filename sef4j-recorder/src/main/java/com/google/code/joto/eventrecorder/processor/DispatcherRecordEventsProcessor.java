package com.google.code.joto.eventrecorder.processor;

import java.util.HashMap;
import java.util.Map;

import com.google.code.joto.eventrecorder.RecordEventSummary;

/**
 * a dispatcher for RecordEventsProcessor, based on RecordEvent type
 */
public class DispatcherRecordEventsProcessor<T> implements RecordEventsProcessor {

	public static class Factory<T> implements RecordEventsProcessorFactory<T> {
		
		private Map<String,RecordEventsProcessorFactory<T>> eventTypeToFactory =
			new HashMap<String,RecordEventsProcessorFactory<T>>();
		
		private RecordEventsProcessorFactory<T> defaultFactory;
		
		public Factory() {
			super();
		}

		public Factory(Map<String,RecordEventsProcessorFactory<T>> eventTypeToFactory,
				RecordEventsProcessorFactory<T> defaultFactory) {
			this();
			this.eventTypeToFactory.putAll(eventTypeToFactory);
			this.defaultFactory = defaultFactory;
		}

		@Override
		public RecordEventsProcessor create(T result) {
			return new DispatcherRecordEventsProcessor<T>(eventTypeToFactory, defaultFactory, result);
		}

		public void registerEventTypeProcessorFactory(String eventType, RecordEventsProcessorFactory<T> eventTypeFactory) {
			eventTypeToFactory.put(eventType, eventTypeFactory);
		}
	}

	private Map<String,RecordEventsProcessorFactory<T>> eventTypeToFactory =
		new HashMap<String,RecordEventsProcessorFactory<T>>();
	private RecordEventsProcessorFactory<T> defaultFactory;


	private Map<String,RecordEventsProcessor> currEventTypeToProcessor =
		new HashMap<String,RecordEventsProcessor>();
	private T result;
	
	//-------------------------------------------------------------------------

	public DispatcherRecordEventsProcessor(
			Map<String,RecordEventsProcessorFactory<T>> eventTypeToFactory,
			RecordEventsProcessorFactory<T> defaultFactory,
			T result) {
		this.eventTypeToFactory = eventTypeToFactory;
		this.defaultFactory = defaultFactory;
		this.result = result;
	}

	@Override
	public boolean needEventObjectData() {
		return true; // ?? may optim
	}

	@Override
	public void processEvent(RecordEventSummary event, Object eventObjectData) {
		// Lookup processor for eventtype, create (and register) if needed
		String eventType = event.getEventType();
		RecordEventsProcessor processor = currEventTypeToProcessor.get(eventType);
		if (processor == null) {
			RecordEventsProcessorFactory<T> factory = eventTypeToFactory.get(eventType);
			if (factory == null) {
				factory = defaultFactory;
			}
			if (factory == null) {
				// TODO??? should throw an event for unrecognized event type, or ignore?
				return;
			}
			processor = factory.create(result);
			currEventTypeToProcessor.put(eventType, processor);
		}

		// do dispatch to processor for eventType
		processor.processEvent(event, eventObjectData);
	}
	

}
