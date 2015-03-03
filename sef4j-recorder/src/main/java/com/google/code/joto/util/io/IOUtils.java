package com.google.code.joto.util.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtils {


	// cf commons-io ??
	public static void closeQuietly(OutputStream stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch(IOException ex) {
				// ignore, no rethrow!
			}
		}
	}

	// cf commons-io ??
	public static void closeQuietly(InputStream stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch(IOException ex) {
				// ignore, no rethrow!
			}
		}
	}

}
