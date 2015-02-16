package org.sef4j.callstack.stats;

import java.security.AccessController;
import java.security.PrivilegedAction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("restriction")
public class UnsafeUtils {

	private static final Logger LOG = LoggerFactory.getLogger(UnsafeUtils.class);
	
	public static final sun.misc.Unsafe UNSAFE = getUnsafe();

    // Cached array base offset
	public static final long ARRAY_BASE_OFFSET = UNSAFE.arrayBaseOffset(byte[].class);;

    public static sun.misc.Unsafe getUnsafe() {
        return AccessController.doPrivileged(new PrivilegedAction<sun.misc.Unsafe>() {
            public sun.misc.Unsafe run() {
		    	try {
		            java.lang.reflect.Field singleoneInstanceField = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
		            singleoneInstanceField.setAccessible(true);
		            sun.misc.Unsafe ret =  (sun.misc.Unsafe)singleoneInstanceField.get(null);
		            return ret;
		        } catch (Throwable e) {
		            LOG.error("Could not instanciate sun.miscUnsafe. should use java.nio DirectByteBuffer ?",e);
		            return null;
		        }
            }
        });
    }

}
