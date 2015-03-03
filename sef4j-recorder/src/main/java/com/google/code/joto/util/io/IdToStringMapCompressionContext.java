package com.google.code.joto.util.io;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * helper class for encoding/decoding String in a contextual compressed way
 */
public class IdToStringMapCompressionContext implements Externalizable {
	
	private int idGenerator = 1;
	private ArrayList<String> idToValue = new ArrayList<String>();
	private Map<String,Integer> valueToId = new HashMap<String,Integer>();

	//-------------------------------------------------------------------------

	public IdToStringMapCompressionContext() {
		clear();
	}

	//-------------------------------------------------------------------------

	public void clear() {
		idToValue.clear();
		valueToId.clear();

		idGenerator = 1;
		idToValue.add(null); // 0=null, real values start at index 1
	}
	
	public void encodeContextualValue(String value, DataOutputStream out) throws IOException {
		if (value == null) {
			out.writeInt(0);
		} else {
			Integer id = valueToId.get(value);
			if (id != null) {
				out.writeInt(id.intValue());
			} else {
				// generate a new unique id... encode as "-id,value"
				// even if the decoder knows the eventIdGenerator value,
				// it is "better" to use a marker for newly generated value
				// benefit: the context can re-read encoded values, by itself or others
				// even it would be no more a "newly" generated id/value
				int newid = idGenerator++;
				id = Integer.valueOf(newid);
				idToValue.add(value);
				valueToId.put(value, id);
				
				out.writeInt(-newid); // with negative sign!
				out.writeUTF(value);
			}
		}
	}

	public String decodeContextualValue(DataInputStream in) throws IOException {
		String res; 
		int id = in.readInt();
		if (id == 0) {
			res = null;
		} else if (id > 0) {
			// "(+)id" : existing value
			res = idToValue.get(id);
		} else { // id < 0
			// "-id,value"
			id = -id;
			res = in.readUTF();
			// register newly id (not needed when re-reading from context)
			if (id > idGenerator) {
				idGenerator = id + 1; //??
				if (idToValue.size() < id) {
					idToValue.ensureCapacity(id);
					for (int i = idToValue.size(); i < id; i++) {
						idToValue.add(null);
					}
				}
				idToValue.set(id, res);
				valueToId.put(res, Integer.valueOf(id));
			} // else re-read value.. 
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
		out.writeInt(idGenerator);
		int size = idToValue.size();
		out.writeInt(size);
		for (int i = 1; i < size; i++) {
			String str = idToValue.get(i);
			out.writeUTF(str);
		}
	}
	
	/**
	 * simpler than java.io.Externalizable: used DataInput instead of ObjectInput..
	 */
	public void readExternal2(DataInput in) throws IOException {
		clear();
		idGenerator = in.readInt();
		int size = in.readInt();
		for (int i = 1; i < size; i++) {
			String str = in.readUTF();
			idToValue.add(str);
			valueToId.put(str, Integer.valueOf(i));
		}
	}

	// override java.lang.Object / debugging helper
	// -------------------------------------------------------------------------
	
	public String toStringSize() {
		return String.valueOf(idGenerator);
	}

	@Override
	public String toString() {
		return "IdToStringCompressionContext[size:" + toStringSize() + "]";
	}
	
}