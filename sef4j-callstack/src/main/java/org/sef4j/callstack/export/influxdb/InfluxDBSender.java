package org.sef4j.callstack.export.influxdb;

import java.util.Collection;
import java.util.Iterator;

import org.sef4j.core.api.EventSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class InfluxDBSender implements EventSender<String> {

	private static final Logger LOG = LoggerFactory.getLogger(InfluxDBSender.class);
	
    /**
     * displayName/url...mainly for display message... 
     * see real connection implementation in sub-classes
     */
    private String url;
    
    
    // ------------------------------------------------------------------------

    public InfluxDBSender(String url) {
    	this.url = url;
    }

    // ------------------------------------------------------------------------

    public abstract void sendJSonBody(String json);
    
	public void sendEvent(String jsonFragment) {
		// wrap body with "[ ... ]"
		String text = "[\n" + jsonFragment + "]\n";
		sendJSonBody(text);
	}
	
	public void sendEvents(Collection<String> jsonFragments) {
		if (jsonFragments == null || jsonFragments.isEmpty()) return;
		// join text with ",\n"  + wrap with "[\n" .. "]\n"
		// see in jdk8 (or apache commons-lang): ...  String.join(",\n", jsonFragments);
		StringBuilder sb = new StringBuilder();
		sb.append("[\n");
		Iterator<String> iter = jsonFragments.iterator(); 
		sb.append(iter.next());
		if (iter.hasNext()) {
			for(; iter.hasNext(); ) {
				String e = iter.next();
				sb.append(",\n");
				sb.append(e);
			}
		}
		String text = sb.toString();
		sb.append("]\n");
		sendJSonBody(text);
	}

	
	@Override
    public String toString() {
        return "InfluxDBSender[url=" + url + "]";
    }

}
