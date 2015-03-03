package com.google.code.joto;

import com.google.code.joto.eventrecorder.RecordEventStore.RecordEventStoreFactory;
import com.google.code.joto.eventrecorder.impl.CyclicBufferRecordEventStore.CyclicBufferRecordEventStoreFactory;
import com.google.code.joto.eventrecorder.impl.DefaultMemoryRecordEventStore.DefaultMemoryRecordEventStoreFactory;
import com.google.code.joto.eventrecorder.impl.FileRecordEventStore.FileRecordEventStoreFactory;
import com.google.code.joto.eventrecorder.impl.RollingFileRecordEventStore.RollingFileRecordEventStoreFactory;
import com.google.code.joto.eventrecorder.predicate.RecordEventSummaryPredicateUtils;
import com.google.code.joto.ui.filter.RecordEventFilterFileUtils;
import com.thoughtworks.xstream.XStream;

public class JotoConfigUtils {

	public static XStream getXStream() {
		XStream xstream = new XStream();
		registerDefaultXStreamAlias(xstream);
		return xstream;
	}
	
	public static void registerDefaultXStreamAlias(XStream res) {
		res.alias("JotoConfig", JotoConfig.class);
		
		res.alias("PrioList", com.google.code.joto.util.PriorityList.class);
		// res.alias("PrioItem", com.google.code.joto.util.PriorityList.Item.class); // private
		
		res.alias("RecordEventStoreFactory", RecordEventStoreFactory.class); 
		res.alias("DefaultMemoryRecordEventStoreFactory", DefaultMemoryRecordEventStoreFactory.class); 
		res.alias("CyclicBufferRecordEventStoreFactory", CyclicBufferRecordEventStoreFactory.class); 
		res.alias("FileRecordEventStoreFactory", FileRecordEventStoreFactory.class); 
		res.alias("RollingFileRecordEventStoreFactory", RollingFileRecordEventStoreFactory.class); 
		
		RecordEventFilterFileUtils.registerDefaultXStreamAlias(res);
		RecordEventSummaryPredicateUtils.registerDefaultXStreamAlias(res);
	}
	

}
