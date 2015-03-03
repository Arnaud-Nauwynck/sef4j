package com.google.code.joto.ui.filter;

import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.event.SwingPropertyChangeSupport;

import org.apache.commons.collections.Predicate;

/**
 * 
 */
public class RecordEventFilterFile {

	private SwingPropertyChangeSupport changeSupport = new SwingPropertyChangeSupport(this);
	
	private String name;

	boolean active = true;
	
	private String description;

	private File persistentFile;

	private Predicate eventPredicate;

	private String eventIdPredicateDescription;
	private String eventDatePredicateDescription;
	private String threadNamePredicateDescription;
	private String eventTypePredicateDescription;
	private String eventSubTypePredicateDescription;
	private String eventClassNamePredicateDescription;
	private String eventMethodNamePredicateDescription;
	private String eventMethodDetailPredicateDescription;
	private String correlatedEventIdPredicateDescription;

	// ------------------------------------------------------------------------

	public RecordEventFilterFile() {
	}

	// ------------------------------------------------------------------------

	public void addPropertyChangeSupport(PropertyChangeListener p) {
		this.changeSupport.addPropertyChangeListener(p);
	}

	public void removePropertyChangeSupport(PropertyChangeListener p) {
		this.changeSupport.removePropertyChangeListener(p);
	}

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean p) {
        boolean old = active;
        this.active = p;
        changeSupport.firePropertyChange("active", old, p);
    }

	public String getName() {
		return name;
	}

	public void setName(String p) {
		String old = this.name;
		this.name = p;
		changeSupport.firePropertyChange("name", old, p);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String p) {
		String old = description;
		this.description = p;
		changeSupport.firePropertyChange("description", old, p);
	}

	public File getPersistentFile() {
		return persistentFile;
	}

	public void setPersistentFile(File p) {
		File old = persistentFile;
		this.persistentFile = p;
		changeSupport.firePropertyChange("persistentFile", old, p);
	}

	public Predicate getEventPredicate() {
		return eventPredicate;
	}

	public void setEventPredicate(Predicate p) {
		Predicate old = eventPredicate;
		this.eventPredicate = p;
		changeSupport.firePropertyChange("eventPredicate", old, p);
	}

	public String getEventIdPredicateDescription() {
		return eventIdPredicateDescription;
	}

	public void setEventIdPredicateDescription(String p) {
		String old = eventIdPredicateDescription;
		this.eventIdPredicateDescription = p;
		changeSupport.firePropertyChange("eventIdPredicateDescription", old, p);
	}

	public String getEventDatePredicateDescription() {
		return eventDatePredicateDescription;
	}

	public void setEventDatePredicateDescription(String p) {
		String old = eventDatePredicateDescription;
		this.eventDatePredicateDescription = p;
		changeSupport.firePropertyChange("eventDatePredicateDescription", old, p);
	}

	public String getThreadNamePredicateDescription() {
		return threadNamePredicateDescription;
	}

	public void setThreadNamePredicateDescription(String p) {
		String old = threadNamePredicateDescription;
		this.threadNamePredicateDescription = p;
		changeSupport.firePropertyChange("threadNamePredicateDescription", old, p);
	}

	public String getEventTypePredicateDescription() {
		return eventTypePredicateDescription;
	}

	public void setEventTypePredicateDescription(String p) {
		String old = eventTypePredicateDescription;
		this.eventTypePredicateDescription = p;
		changeSupport.firePropertyChange("eventTypePredicateDescription", old, p);
	}

	public String getEventSubTypePredicateDescription() {
		return eventSubTypePredicateDescription;
	}

	public void setEventSubTypePredicateDescription(String p) {
		String old = eventSubTypePredicateDescription;
		this.eventSubTypePredicateDescription = p;
		changeSupport.firePropertyChange("eventSubTypePredicateDescription", old, p);
	}

	public String getEventClassNamePredicateDescription() {
		return eventClassNamePredicateDescription;
	}

	public void setEventClassNamePredicateDescription(String p) {
		String old = eventClassNamePredicateDescription;
		this.eventClassNamePredicateDescription = p;
		changeSupport.firePropertyChange("eventClassNamePredicateDescription", old, p);
	}

	public String getEventMethodNamePredicateDescription() {
		return eventMethodNamePredicateDescription;
	}

	public void setEventMethodNamePredicateDescription(String p) {
		String old = eventMethodNamePredicateDescription;
		this.eventMethodNamePredicateDescription = p;
		changeSupport.firePropertyChange("eventMethodNamePredicateDescription", old, p);
	}

	public String getEventMethodDetailPredicateDescription() {
		return eventMethodDetailPredicateDescription;
	}

	public void setEventMethodDetailPredicateDescription(String p) {
		String old = eventMethodDetailPredicateDescription;
		this.eventMethodDetailPredicateDescription = p;
		changeSupport.firePropertyChange("eventMethodDetailPredicateDescription", old, p);
	}

	public String getCorrelatedEventIdPredicateDescription() {
		return correlatedEventIdPredicateDescription;
	}

	public void setCorrelatedEventIdPredicateDescription(String p) {
		String old = correlatedEventIdPredicateDescription;
		this.correlatedEventIdPredicateDescription = p;
		changeSupport.firePropertyChange("correlatedEventIdPredicateDescription", old, p);
	}

	
	public void set(RecordEventFilterFile src) {
		this.name = src.name;
		this.description = src.description;
		this.persistentFile = src.persistentFile;

		this.eventPredicate = src.eventPredicate;

		this.eventIdPredicateDescription = src.eventIdPredicateDescription;
		this.eventDatePredicateDescription = src.eventDatePredicateDescription;
		this.threadNamePredicateDescription = src.threadNamePredicateDescription;
		this.eventTypePredicateDescription = src.eventTypePredicateDescription;
		this.eventSubTypePredicateDescription = src.eventSubTypePredicateDescription;
		this.eventClassNamePredicateDescription = src.eventClassNamePredicateDescription; 
		this.eventMethodNamePredicateDescription = src.eventMethodNamePredicateDescription;
		this.eventMethodDetailPredicateDescription = src.eventMethodDetailPredicateDescription;
		this.correlatedEventIdPredicateDescription = src.correlatedEventIdPredicateDescription;

		changeSupport.firePropertyChange("all", Boolean.FALSE, this);
	}

	// ------------------------------------------------------------------------
	
	@Override
	public String toString() {
		return "RecordEventFilterItem [" 
				+ "name=" + name 
				+ ", description=" + description 
				+ ", persistentFile=" + persistentFile 
				+ "]";
	}

	
}
