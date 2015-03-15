package org.sef4j.callstack.export.influxdb.series;


public class SerieColNameUtil {

    public static String wrapName(String prefix, String name, String suffix) {
    	if (prefix == null) prefix = "";
    	if (suffix == null) suffix = "";
    	boolean noCapitalizeFirstLetter = prefix.isEmpty() || prefix.endsWith("_");
    	String name2 = noCapitalizeFirstLetter? name : Character.toUpperCase(name.charAt(0)) + name.substring(1);
    	return prefix + name2 + suffix;
    }

	public static String prefixed(String prefix, String name) {
		return wrapName(prefix, name, "");
	}

    public static String[] wrapNames(String prefix, String[] wrappedColNames, String suffix) {
    	final int len = wrappedColNames.length;
    	final String[] res = new String[len];
    	for(int i = 0; i < len; i++) {
    		res[i] = wrapName(prefix, wrappedColNames[i], suffix);
    	}
    	return res;
    }
    
    public static String[] wrapNamesRange(String prefix, String[] wrappedColNames, String suffix, int from, int to) {
    	final int colNameLen = wrappedColNames.length;
    	final int totalLen = colNameLen * (to - from);
    	final String[] res = new String[totalLen];
    	final String[] colNames = wrapNames(prefix, wrappedColNames, suffix);
 	    int index = 0;
 	    for(int i = from; i < to; i++) {
 	    	for (int j = 0; j < colNameLen; j++) {
 	    		res[index++] = colNames[j] + i;
 	    	}
	    }
 	    return res;
    }

    
}
