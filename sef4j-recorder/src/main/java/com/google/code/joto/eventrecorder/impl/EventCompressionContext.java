package com.google.code.joto.eventrecorder.impl;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.OutputStream;
import java.util.Date;

import com.google.code.joto.eventrecorder.RecordEventSummary;
import com.google.code.joto.util.io.CompressedObjectInputStream;
import com.google.code.joto.util.io.CompressedObjectOutputStream;
import com.google.code.joto.util.io.IdToObjectStreamClassCompressionContext;
import com.google.code.joto.util.io.IdToStringMapCompressionContext;

/**
 * internal helper class for encoding/decoding EventRecordSummary 
 * in a contextual compressed way
 */
public class EventCompressionContext implements Externalizable {

	private IdToStringMapCompressionContext threadNameCtx = new IdToStringMapCompressionContext();
	private IdToStringMapCompressionContext eventTypeCtx = new IdToStringMapCompressionContext(); 
	private IdToStringMapCompressionContext eventSubTypeCtx = new IdToStringMapCompressionContext(); 
	private IdToStringMapCompressionContext eventClassNameCtx = new IdToStringMapCompressionContext();
	private IdToStringMapCompressionContext eventMethodNameCtx = new IdToStringMapCompressionContext();
	// not compressed?? private IdToStringMapCompressionContext eventMethodDetail;

	private IdToObjectStreamClassCompressionContext objectStreamClassCtx =
		new IdToObjectStreamClassCompressionContext();
	
	// -------------------------------------------------------------------------
	
	public EventCompressionContext() {
	}

	//-------------------------------------------------------------------------

	
	public void encodeContextualRecordEventSummary(RecordEventSummary src, DataOutputStream out) throws IOException {
		// not encoded here... eventId;
		out.writeLong(src.getEventDate().getTime());
		threadNameCtx.encodeContextualValue(src.getThreadName(), out);
		eventTypeCtx.encodeContextualValue(src.getEventType(), out);
		eventSubTypeCtx.encodeContextualValue(src.getEventSubType(), out);
		eventClassNameCtx.encodeContextualValue(src.getEventClassName(), out);
		eventMethodNameCtx.encodeContextualValue(src.getEventMethodName(), out);
		String eventMethodDetail = src.getEventMethodDetail();
		out.writeBoolean(eventMethodDetail != null);
		if (eventMethodDetail != null) {
			out.writeUTF(eventMethodDetail);
		}
		// not encoded here... internalEventStoreDataAddress;
	}
	
	public RecordEventSummary decodeContextualRecordEventSummary(
			int eventId, DataInputStream in
			) throws IOException {
		RecordEventSummary res = new RecordEventSummary(eventId);
		
		// not decoded here... eventId
		res.setEventDate(new Date(in.readLong()));
		res.setThreadName(threadNameCtx.decodeContextualValue(in));
		res.setEventType(eventTypeCtx.decodeContextualValue(in));
		res.setEventSubType(eventSubTypeCtx.decodeContextualValue(in));
		res.setEventClassName(eventClassNameCtx.decodeContextualValue(in));
		res.setEventMethodName(eventMethodNameCtx.decodeContextualValue(in));
		boolean isEventMethodDetail = in.readBoolean();
		if (isEventMethodDetail) {
			res.setEventMethodDetail(in.readUTF());
		}
		// not decoded here... internalEventStoreDataAddress;

		return res;
	}
	
	// encode/decode for ObjectData
	// -------------------------------------------------------------------------
	
	public void encodeContextualObjectData(Object objectData, OutputStream out) throws IOException {
		CompressedObjectOutputStream compObjOut = 
			new CompressedObjectOutputStream(out, objectStreamClassCtx);
		compObjOut.writeObject(objectData);
		
	}

	public Object decodeContextualObjectData(InputStream in) throws IOException {
		Object res;
		try {
			CompressedObjectInputStream compObjIn = 
				new CompressedObjectInputStream(in, objectStreamClassCtx);
			res = compObjIn.readObject();
		} catch(ClassNotFoundException ex) {
			throw new IOException("failed to decode tmp compressed obj", ex);
		}
		return res;
	}
	
	// -------------------------------------------------------------------------
	
	/**
	 * implements java.io.Externalizable (optim for Serializable)
	 * => used to encode self ... do not mismatch with encodeContextualValue! 
	 */
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		writeExternal2(out);		
	}
	
	/**
	 * implements java.io.Externalizable (optim for Serializable)
	 * => used to decode self ... do not mismatch with decodeContextualValue! 
	 */
	@Override
	public void readExternal(ObjectInput in) throws IOException {
		readExternal2(in);
	}

	/**
	 * simpler than java.io.Externalizable: used DataOutput instead of ObjectOutput..
	 */
	public void writeExternal2(DataOutput out) throws IOException {
		threadNameCtx.writeExternal2(out);
		eventTypeCtx.writeExternal2(out); 
		eventSubTypeCtx.writeExternal2(out); 
		eventClassNameCtx.writeExternal2(out);
		eventMethodNameCtx.writeExternal2(out);
		
		objectStreamClassCtx.writeExternal2(out);
	}
	
	/**
	 * simpler than java.io.Externalizable: used DataInput instead of ObjectInput.. 
	 */
	public void readExternal2(DataInput in) throws IOException {
		threadNameCtx.readExternal2(in);
		eventTypeCtx.readExternal2(in); 
		eventSubTypeCtx.readExternal2(in);
		eventClassNameCtx.readExternal2(in);
		eventMethodNameCtx.readExternal2(in);

		objectStreamClassCtx.readExternal2(in);
	}

	public void clear() {
		threadNameCtx.clear();
		eventTypeCtx.clear(); 
		eventSubTypeCtx.clear(); 
		eventClassNameCtx.clear();
		eventMethodNameCtx.clear();
		
		objectStreamClassCtx.clear();
	}

	// override java.lang.Object / debugging helper
	// -------------------------------------------------------------------------
	
	public String toStringSizes() {
		return "thread:" + threadNameCtx.toStringSize()
			+ ", type:" + eventTypeCtx.toStringSize()
			+ ", subType:" + eventSubTypeCtx.toStringSize() 
			+ ", className:" + eventClassNameCtx.toStringSize()
			+ ", methodName:" + eventMethodNameCtx.toStringSize()
			+ ", objectStreamClass:" + objectStreamClassCtx.toStringSize()
			+ "]";
	}

	@Override
	public String toString() {
		return "EventCompressionContext[sizes.." + toStringSizes() + "]";
	}

}
