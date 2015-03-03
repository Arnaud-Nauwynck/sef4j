package com.google.code.joto.eventrecorder.writer;

import java.beans.PropertyChangeListener;
import java.io.Serializable;

import com.google.code.joto.eventrecorder.RecordEventSummary;

/**
 *
 */
public interface RecordEventWriter {

	public void addPropertyChangeListener(PropertyChangeListener listener);
	public void removePropertyChangeListener(PropertyChangeListener listener);

	/** @return false when writer is disabled */
	public boolean isEnable();

	/** @return false when <code>info</code> event type is filtered out */
	public boolean isEnable(RecordEventSummary info);

	public void addEvent(RecordEventSummary info,
			Serializable objData, 
			RecordEventWriterCallback callback);

}