package com.google.code.joto.util.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

/**
 * specific sub-class of ObjectInputStream for compressing
 */
public class CompressedObjectInputStream extends ObjectInputStream {

	private IdToObjectStreamClassCompressionContext ctx;
	
	// -------------------------------------------------------------------------
	
	public CompressedObjectInputStream(
			InputStream input,
			IdToObjectStreamClassCompressionContext ctx)
			throws IOException, SecurityException {
		super(input);
		this.ctx = ctx;
	}

	// -------------------------------------------------------------------------
	
	@Override
	protected ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException {
		// TOCHECK use sub-block for underlying input 
		ObjectStreamClass res = ctx.decodeContextualValue(this);
		return res;
	}

}
