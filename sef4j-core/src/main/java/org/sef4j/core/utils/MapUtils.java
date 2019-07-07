package org.sef4j.core.utils;

import java.util.Map;

public class MapUtils {

    /** static utility method to extract boolean option from map */
    public static boolean mapGetBooleanOption(Map<String, Object> options, String keyName, boolean defaultValue) {
	boolean res = defaultValue;
	if (options != null) {
	    Object resObj = options.get(keyName);
	    if (resObj != null) {
		if (resObj instanceof Boolean) {
		    res = ((Boolean) resObj).booleanValue();
		} else if (resObj instanceof String) {
		    res = Boolean.valueOf((String) resObj);
		} // else unrecognized option
	    }
	}
	return res;
    }

}
