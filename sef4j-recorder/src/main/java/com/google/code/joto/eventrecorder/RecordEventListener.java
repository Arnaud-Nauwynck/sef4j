package com.google.code.joto.eventrecorder;

import java.util.List;

/**
 *
 */
public interface RecordEventListener {

	public void onEvent(RecordEventStoreChange event);
	public void onEvents(List<RecordEventStoreChange> event);
	
}
