package com.google.code.joto.eventrecorder.processor;

/**
 * factory interface for creating RecordEventsProcessor  
 */
public interface RecordEventsProcessorFactory<TResult> {

	/**
	 * @param res the result to feed by processor
	 */
	public RecordEventsProcessor create(TResult res);
	
}
