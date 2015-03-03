package com.google.code.joto.util.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.Externalizable;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * helper class for encoding/decoding ObjectStreamClass in a contextual compressed way
 */
public class IdToObjectStreamClassCompressionContext implements Externalizable {
	
	private int idGenerator = 1;
	private ArrayList<ObjectStreamClass> idToValue = new ArrayList<ObjectStreamClass>();
	private Map<Class<?>,Integer> valueToId = new HashMap<Class<?>,Integer>();

	//-------------------------------------------------------------------------

	public IdToObjectStreamClassCompressionContext() {
		clear();
	}

	//-------------------------------------------------------------------------

	public void clear() {
		idToValue.clear();
		valueToId.clear();

		idGenerator = 1;
		idToValue.add(null); // add NULL value
		
		// TOADD register default classes ..
		// for String,List,Integer,Boolean,...
	}
	
	public void encodeContextualValue(ObjectStreamClass valueClassData, ObjectOutputStream out) throws IOException {
		if (valueClassData == null) {
			out.writeInt(0);
		} else {
			Class<?> value = valueClassData.forClass();
			Integer id = valueToId.get(value);
			if (id != null) {
				out.writeInt(id.intValue());
			} else {
				// generate a new unique id... encode as "-id,value"
				// even if the decoder knows the eventIdGenerator value,
				// it is "better" to use a marker for newly generated value
				// benefit: the context can re-read encoded values, by itself or others
				// even it would be no more a "newly" generated id/value
				
				int newid = addNewValue(valueClassData);
				
				out.writeInt(-newid); // with negative sign!
				doWriteValue(valueClassData, out);
			}
		}
	}

	public ObjectStreamClass decodeContextualValue(ObjectInputStream in) throws IOException {
		ObjectStreamClass res; 
		int id = in.readInt();
		if (id == 0) {
			res = null;
		} else if (id > 0) {
			// "(+)id" : existing value
			res = idToValue.get(id);
		} else { // id < 0
			// "-id,value"
			id = -id;
			res = doReadValue(in);
			
			Class<?> value = res.forClass();
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
				valueToId.put(value, Integer.valueOf(id));
			} // else re-read value.. 
		}
		return res;
	}

	
	private ObjectStreamClass doReadValue(DataInput in) throws IOException {
		ObjectStreamClass res;
		String className = in.readUTF(); // TODO decode read real ObjectStreamClass instead of className?... cf corresponding doWriteValue()
		Class<?> clss;
		try {
			clss = Class.forName(className);
		} catch(Exception ex) {
			throw new InvalidClassException(className);
		}
		res = ObjectStreamClass.lookup(clss);
		return res;
	}

	private void doWriteValue(ObjectStreamClass classData, DataOutput out) throws IOException {
		Class<?> value = classData.forClass();
		out.writeUTF(value.getName()); // TODO encode real valueClassData instead of name??? .. cf corresponding doReadValue()
	}

	// ObjectStreamClass classData = ObjectStreamClass.lookup(value);
	protected int addNewValue(ObjectStreamClass classData) {
		int newid = idGenerator++;
		idToValue.add(classData);
		Class<?> value = classData.forClass();
		valueToId.put(value, Integer.valueOf(newid));
		return newid;
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
			ObjectStreamClass valueClassData = idToValue.get(i);
			doWriteValue(valueClassData, out);
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
			ObjectStreamClass valueClassData = doReadValue(in);
			Class<?> value = valueClassData.forClass();
			idToValue.add(valueClassData);
			valueToId.put(value, Integer.valueOf(i));
		}
	}


	// override java.lang.Object / debugging helper
	// -------------------------------------------------------------------------
	
	@Override
	public String toString() {
		return "IdToObjectStreamClassCompressionContext[size:" + toStringSize() + "]";
	}
	
	public String toStringSize() {
		return String.valueOf(idGenerator);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IdToObjectStreamClassCompressionContext other = (IdToObjectStreamClassCompressionContext) obj;
		if (idGenerator != other.idGenerator)
			return false;
		if (idToValue == null) {
			if (other.idToValue != null)
				return false;
		} else if (!idToValue.equals(other.idToValue))
			return false;
		return true;
	}


}
