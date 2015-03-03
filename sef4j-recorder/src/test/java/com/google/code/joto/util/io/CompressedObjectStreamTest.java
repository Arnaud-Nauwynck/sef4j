package com.google.code.joto.util.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import junit.framework.TestCase;

import com.google.code.joto.testobj.TestObjFactory;

/**
 * JUnit test for 
 * CompressedObjectInputStream/CompressedObjectOutputStream,
 * IdToObjectStreamClassCompressionContext
 *
 */
public class CompressedObjectStreamTest extends TestCase {

	public CompressedObjectStreamTest(String name) {
		super(name);
	}

	public void testInOut() {
		IdToObjectStreamClassCompressionContext ctx = 
			new IdToObjectStreamClassCompressionContext();
		for (int i = 0; i < 6; i++) {
			Serializable obj = TestObjFactory.createAnySerializableBean(i);
			
			// test in new ctx
			doTestInOut(obj);
			
			// test in global ctx
			doTestInOut(ctx, obj);
			
			// repeat test in global ctx
			doTestInOut(ctx, obj);
		}
	}
	
	protected void doTestInOut(
			IdToObjectStreamClassCompressionContext ctx, 
			Object obj) {
		try {
			ByteArrayOutputStream bufOut = new ByteArrayOutputStream();
			ObjectOutputStream compObjOut = new CompressedObjectOutputStream(bufOut, ctx);
			compObjOut.writeObject(obj);
		
			ByteArrayInputStream bufIn = new ByteArrayInputStream(bufOut.toByteArray());
			ObjectInputStream compObjIn = new CompressedObjectInputStream(bufIn, ctx);
			Object objCopy = compObjIn.readObject();
			if (obj == null) {
				assertNull(objCopy);
			} else {
				assertEquals(obj.getClass(), objCopy.getClass());
				// TODO more precise check??
			}
			
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	protected void doTestInOut(Object obj) {
		IdToObjectStreamClassCompressionContext ctx = 
			new IdToObjectStreamClassCompressionContext();

		doTestInOut(ctx, obj);

		// repeat in ctx
		doTestInOut(ctx, obj);
	}

	
}
