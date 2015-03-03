package com.google.code.joto.eventrecorder.writer;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import com.google.code.joto.eventrecorder.RecordEventSummary;

/**
 *
 */
public abstract class AbstractRecordEventWriter implements RecordEventWriter {

	protected PropertyChangeSupport changeSupport = new PropertyChangeSupport(this); 

	// ------------------------------------------------------------------------

	protected AbstractRecordEventWriter() {
	}
	
	// ------------------------------------------------------------------------
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.removePropertyChangeListener(listener);
	}

	@Override
	public boolean isEnable() {
		return true;
	}

	@Override
	public boolean isEnable(RecordEventSummary info) {
		return true;
	}
	
	
}
