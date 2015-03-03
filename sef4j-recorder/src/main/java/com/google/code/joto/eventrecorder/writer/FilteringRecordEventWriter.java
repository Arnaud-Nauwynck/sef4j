package com.google.code.joto.eventrecorder.writer;

import com.google.code.joto.eventrecorder.RecordEventData;
import com.google.code.joto.eventrecorder.RecordEventSummary;
import com.google.code.joto.eventrecorder.predicate.RecordEventSummaryPredicate;

import org.apache.commons.collections.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * proxy implementation of RecordEventWriter to add filtering
 * with global boolean, or List<RecordEventSummaryPredicate>
 */
public class FilteringRecordEventWriter extends AbstractRecordEventWriter implements RecordEventWriter {
	
	private static Logger log = LoggerFactory.getLogger(FilteringRecordEventWriter.class);
	
	/** underlying proxy target object */
	private RecordEventWriter target;
	
	protected boolean enable = true;

	/**
	 * list of predicate (filters) to filter on RecordEventSummary elements
	 */
	protected List<Predicate> eventPredicates = new ArrayList<Predicate>();
	
	/** optional */
	private Object owner;
    /** optional */
    private String name;
	
	// -------------------------------------------------------------------------
	
	public FilteringRecordEventWriter(RecordEventWriter target) {
		this.target = target;
	}

	// -------------------------------------------------------------------------

	@Override
	public void addEvent(RecordEventSummary info, Serializable objData, RecordEventWriterCallback callback) {
		if (enable == false || !isEnable(info)) {
			if (callback != null) {
				RecordEventData dummy = new RecordEventData(info, objData);			
				callback.onStore(dummy);
			}
			return;
		}
		
		// delegate to underlying
		target.addEvent(info, objData, callback);
	}
	
	
	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean p) {
		if (p != enable) {
			boolean oldValue = enable;
			this.enable = p;
			changeSupport.firePropertyChange("enable", oldValue, p);
		}
	}

	public List<Predicate> getEventPredicates() {
		return Collections.unmodifiableList(eventPredicates);
	}

	public void setEventPredicateElts(List<Predicate> p) {
        List<Predicate> old = new ArrayList<Predicate>(eventPredicates);
        eventPredicates.clear();
        if (p != null) {
            eventPredicates.addAll(p);
        }
        changeSupport.firePropertyChange("eventPredicates", old, eventPredicates);
    }

	public void addEventPredicate(RecordEventSummaryPredicate p) {
		List<Predicate> old = new ArrayList<Predicate>(eventPredicates);
		eventPredicates.add(p);
		changeSupport.firePropertyChange("eventPredicates", old, eventPredicates);
	}
	
	public void removeEventPredicate(Predicate p) {
		List<Predicate> old = new ArrayList<Predicate>(eventPredicates);
		eventPredicates.add(p);
		changeSupport.firePropertyChange("eventPredicates", old, eventPredicates);
	}

	
	public boolean isEnable(RecordEventSummary eventInfo) {
		boolean res = true;
		if (eventInfo.getEventMethodName() != null 
		        && eventInfo.getEventMethodName().endsWith("UserAdvancement")) {
		    log.debug("event: " + eventInfo.getEventMethodName());
		}
		if (eventPredicates != null && !eventPredicates.isEmpty()) {
			for(Predicate pred : eventPredicates) {
				if (!pred.evaluate(eventInfo)) {
					res = false;
					break;
				}
			}
		}
		return res;
	}

    public Object getOwner() {
        return owner;
    }

    public void setOwner(Object p) {
        this.owner = p;
    }

    public String getName() {
        return name;
    }

    public void setName(String p) {
        this.name = p;
    }
    
}
