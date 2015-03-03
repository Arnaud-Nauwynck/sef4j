package com.google.code.joto.util.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.google.code.joto.util.JotoRuntimeException;
import com.thoughtworks.xstream.XStream;

/**
 * utility method for XStream
 */
public class XStreamUtils {

	public static void toFile(XStream xstream, Object obj, File file) {
		OutputStream out = null;
		try {
			out = new BufferedOutputStream(new FileOutputStream(file));
			xstream.toXML(obj, out);
		} catch(IOException ex) {
			throw JotoRuntimeException.wrapRethrow("Failed to write file '" + file.getAbsolutePath() + "'", ex);
		} catch(Exception ex) {
			throw JotoRuntimeException.wrapRethrow("Failed to write xml file '" + file.getAbsolutePath() + "'", ex);
		} finally {
			IOUtils.closeQuietly(out);
		}
	}


	public static Object fromFile(XStream xstream, File file) {
		Object res = null;
		InputStream in = null;
		if (!file.exists()) {
			throw new JotoRuntimeException("File not found '" + file.getAbsolutePath() + "'");
		}
		try {
			in = new BufferedInputStream(new FileInputStream(file));
			res = xstream.fromXML(in);
		} catch(IOException ex) {
			throw JotoRuntimeException.wrapRethrow("Failed to read file '" + file.getAbsolutePath() + "'", ex);
		} catch(Exception ex) {
			throw JotoRuntimeException.wrapRethrow("Failed to read xml file '" + file.getAbsolutePath() + "'", ex);
		} finally {
			IOUtils.closeQuietly(in);
		}
		return res;
	}


}
