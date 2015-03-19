package org.sef4j.callstack.export.influxdb;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.Iterator;

import org.sef4j.core.api.EventSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class InfluxDBJsonSender implements EventSender<String> {

	private static final Logger LOG = LoggerFactory.getLogger(InfluxDBJsonSender.class);

	
    /**
     * displayName/url...mainly for display message... 
     * see real connection implementation in sub-classes
     */
    protected String url;
    
    
    private int warnElapsedThreshold = 20*1000; // 20 seconds
    private int countSent = 0;
    private int countSentFailed = 0;
    private int countSentSlow = 0;
    
    
    // ------------------------------------------------------------------------

    public InfluxDBJsonSender(String url) {
    	this.url = url;
    }

    // ------------------------------------------------------------------------

    protected abstract void doSendJSonBody(byte[] json);

    public void sendJSonBody(byte[] json) {
        countSent++;
        long startTime = System.currentTimeMillis();
        try {
            
            doSendJSonBody(json);
            
            long timeMillis = System.currentTimeMillis() - startTime;
            if (timeMillis > warnElapsedThreshold) {
                countSentSlow++;
            }
        } catch(RuntimeException ex){
            countSentFailed++;
            LOG.warn("Failed to send json to InfluxDB '" + url + "' ... rethrow", ex);
        }
    }
    
	public void sendEvent(String jsonFragment) {
		// wrap body with "[ ... ]"
		String text = "[\n" + jsonFragment + "]\n";
		sendJSonBody(text.getBytes());
	}
	
	public void sendEvents(Collection<String> jsonFragments) {
		if (jsonFragments == null || jsonFragments.isEmpty()) return;
		// join text with ",\n"  + wrap with "[\n" .. "]\n"
		// see in jdk8 (or apache commons-lang): ...  String.join(",\n", jsonFragments);
		ByteArrayOutputStream buffer = new ByteArrayOutputStream(1024); 
		OutputStreamWriter out = new OutputStreamWriter(buffer);
		try {
    		out.append("[\n");
    		Iterator<String> iter = jsonFragments.iterator(); 
    		out.append(iter.next());
    		if (iter.hasNext()) {
    			for(; iter.hasNext(); ) {
    				String e = iter.next();
    				out.append(",\n");
    				out.append(e);
    			}
    		}
    		out.append("]\n");
    		out.flush();
		} catch(IOException ex) {
		    // in memory buffer ... IOException should not occurs!
		}
		sendJSonBody(buffer.toByteArray());
	}

	
	// ------------------------------------------------------------------------

	public int getWarnElapsedThreshold() {
        return warnElapsedThreshold;
    }

    public void setWarnElapsedThreshold(int warnElapsedThreshold) {
        this.warnElapsedThreshold = warnElapsedThreshold;
    }

    public int getCountSent() {
        return countSent;
    }

    public void setCountSent(int countSent) {
        this.countSent = countSent;
    }

    public int getCountSentFailed() {
        return countSentFailed;
    }

    public void setCountSentFailed(int countSentFailed) {
        this.countSentFailed = countSentFailed;
    }

    public int getCountSentSlow() {
        return countSentSlow;
    }

    public void setCountSentSlow(int countSentSlow) {
        this.countSentSlow = countSentSlow;
    }

    // ------------------------------------------------------------------------
    
    @Override
    public String toString() {
        return "InfluxDBSender[url=" + url + "]";
    }

}
