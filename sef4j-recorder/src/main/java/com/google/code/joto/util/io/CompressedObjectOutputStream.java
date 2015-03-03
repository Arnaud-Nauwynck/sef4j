package com.google.code.joto.util.io;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;

/**
 * specific sub-class of ObjectOutputStream for compressing
 */
public class CompressedObjectOutputStream extends ObjectOutputStream {

	private IdToObjectStreamClassCompressionContext ctx;
	
	// -------------------------------------------------------------------------
	
	public CompressedObjectOutputStream(
			OutputStream output,
			IdToObjectStreamClassCompressionContext ctx)
			throws IOException, SecurityException {
		super(output);
		this.ctx = ctx;
	}

	// -------------------------------------------------------------------------
	
	@Override
	protected void writeClassDescriptor(ObjectStreamClass desc) throws IOException {
		// TOCHECK use sub-block for underlying output
		ctx.encodeContextualValue(desc, this);
	}

	// -------------------------------------------------------------------------
	
	

}
