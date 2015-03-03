package com.google.code.joto.eventrecorder;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * default implementation of RecordEventListener for redispatching to a RecordEventVisitor
 */
public class DefaultVisitorRecordEventListener implements RecordEventListener {

	private static Logger log = LoggerFactory.getLogger(DefaultVisitorRecordEventListener.class);
	
	private RecordEventChangeVisitor target;
	
	//-------------------------------------------------------------------------

	public DefaultVisitorRecordEventListener(RecordEventChangeVisitor target) {
		this.target = target;
	}

	//-------------------------------------------------------------------------

	
	public void onEvent(RecordEventStoreChange event) {
		onEventStoreEvent(event);
	}
	
	public void onEvents(List<RecordEventStoreChange> events) {
		for(RecordEventStoreChange event : events) {
			onEventStoreEvent(event);
		}
	}
	
	protected void onEventStoreEvent(RecordEventStoreChange event) {
		try {
			event.accept(target);			
		} catch(Exception ex) {
			// ignore, no rethrow!
			log.warn("Failed to handle RecordEvent...ignore!", ex);
		}
	}
}
