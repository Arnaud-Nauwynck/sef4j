package com.google.code.joto.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * utility RuntimeException sub-class ...
 * to avoid try-catch on Check exceptions without rethrow
 *
 */
public class JotoRuntimeException extends RuntimeException {

	/** internal for java.io.Serializable */
	private static final long serialVersionUID = 1L;
	
	private static Logger log = LoggerFactory.getLogger(JotoRuntimeException.class);
	
	// ------------------------------------------------------------------------
	
	public JotoRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public JotoRuntimeException(String message) {
		super(message);
	}

	public JotoRuntimeException(Throwable cause) {
		super(cause);
	}

	// ------------------------------------------------------------------------
	
	public static JotoRuntimeException wrap(String msg, Exception ex) {
		JotoRuntimeException res;
		if (ex instanceof JotoRuntimeException) {
			res = (JotoRuntimeException) ex;
			// message is not enriched in current impl (should do?)
		} else {
			res = new JotoRuntimeException(msg, ex);
		}
		return res;
	}

	/**
	 * tipical usage:
	 * <code> try { .. } catch(Exception ex) { throw JotoRuntimeException.wrapRethrow(msg, ex); } </code>
	 * @param msg
	 * @param ex
	 * @return
	 */
	public static JotoRuntimeException wrapRethrow(String msg, Exception ex) {
		JotoRuntimeException wrapEx = wrap(msg, ex);
		throw wrapEx; // force rethrow even if usually call with "throw wrapRethrow(msg, ex);" for code violations warning  
	}

	public static void NOT_IMPLEMENTED_YET() {
		log.error("TODO NOT_IMPLEMENTED_YET");
		throw new JotoRuntimeException("Not implemented yet!!"); 
	}

}
