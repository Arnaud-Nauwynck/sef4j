package com.google.code.joto;

import com.google.code.joto.eventrecorder.RecordEventStore.RecordEventStoreFactory;
import com.google.code.joto.eventrecorder.impl.DefaultMemoryRecordEventStore.DefaultMemoryRecordEventStoreFactory;
import com.google.code.joto.ui.tree.AggrRecordEventTemplatizerDispatcher;
import com.google.code.joto.value2java.ObjectVHToStmtConverter;
import com.google.code.joto.value2java.VHToStmtConverterLookup;

/**
 *
 */
public class JotoConfig {

	private RecordEventStoreFactory eventStoreFactory; 
		
	private VHToStmtConverterLookup vhToStmtConverterLookup = 
		new VHToStmtConverterLookup(true);

	/** pluggable dispatcher mecanism for choosing RecordEventTemplatizer per event types <BR/>
	 * used to build an aggregated Tree of events, with templatized params + statistics count 
	 */
	private AggrRecordEventTemplatizerDispatcher eventTemplatizerDispatcher = new AggrRecordEventTemplatizerDispatcher();

	
	// ------------------------------------------------------------------------

	public JotoConfig() {
	}

	// ------------------------------------------------------------------------

	public RecordEventStoreFactory getEventStoreFactory() {
		if (eventStoreFactory == null) {
			eventStoreFactory = new DefaultMemoryRecordEventStoreFactory();
		}
		return eventStoreFactory;
	}

	public void setEventStoreFactory(RecordEventStoreFactory p) {
		this.eventStoreFactory = p;
	}

	public VHToStmtConverterLookup getVhToStmtConverterLookup() {
		return vhToStmtConverterLookup;
	}

	public void setVhToStmtConverterLookup(VHToStmtConverterLookup p) {
		this.vhToStmtConverterLookup = p;
	}

	public ObjectVHToStmtConverter lookupConverter(Class<?> type) {
		return vhToStmtConverterLookup.lookupConverter(type);
	}

	public AggrRecordEventTemplatizerDispatcher getEventTemplatizerDispatcher() {
		return eventTemplatizerDispatcher;
	}

	public void setEventTemplatizerDispatcher(AggrRecordEventTemplatizerDispatcher p) {
		this.eventTemplatizerDispatcher = p;
	}
	
}
