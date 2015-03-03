package com.google.code.joto.util.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * utility class for Serialization
 */
public class SerializableUtil {

	/** private to force all static */
	private SerializableUtil() {}
	
	/** ObjectInputStream utility method */
	public static Object byteArrayToSerializable(byte[] data) {
		Object res;
		try {
			ByteArrayInputStream bin = new ByteArrayInputStream(data); 
			ObjectInputStream oin = new ObjectInputStream(bin);
			res = oin.readObject();
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		}
		return res;
	}

	/** ObjectOutputStream utility method */
	public static byte[] serializableToByteArray(Serializable objectData) {
		byte[] res;
		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream(); 
			ObjectOutputStream oout = new ObjectOutputStream(bout);
			oout.writeObject(objectData);
			oout.flush();
			res = bout.toByteArray();
		} catch(Exception ex) {
			throw new RuntimeException("Failed to serialize obj data", ex);
		}
		return res;
	}

	public static boolean checkSerializable(Object obj) {
		if (obj == null) return true;
		if (!(obj instanceof Serializable) 
				// && !(obj instanceof Externalizable) .. cf Serializable
				) {
			return false;
		}
		boolean res;
		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream(); 
			ObjectOutputStream oout = new ObjectOutputStream(bout);
			oout.writeObject(obj);
			res = true;
		} catch(Exception ex) {
			res = false; // ignore, no rethrow!
		}
		return res;
	}
	
}
