package org.sef4j.callstack.export.influxdb;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.Iterator;

import org.sef4j.core.api.EventSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * EventSender<String> to send Json text fragments to InfluxDB<br/>
 * <BR/>
 * Each String event to send is supposed to be formatted as a Json fragment : "{ ... }"<BR/>
 * This class may join multiple fragments in a single call, as"[ frag1, frag2, ... fragN ]"<BR/> 
 * 
 * see json formatter helper class, to convert stat value object to Json text
 *  @see org.sef4j.callstack.export.influxdb.jsonprinters.AbstractInfluxDBValuePrinter
 *  and all sub-classes: PerfStatsInfluxDBPrinter, BasicTimeStatsLogHistogramInfluxDBPrinter, PendingPerfCountInfluxDBPrinter 
 *  
 */
public abstract class InfluxDBJsonSender implements EventSender<String> {

	private static final Logger LOG = LoggerFactory.getLogger(InfluxDBJsonSender.class);

	
    /**
     * displayName/url...mainly for display message... 
     * see real connection implementation in sub-classes
     */
    protected String displayUrl;
    
    
    private int warnElapsedThreshold = 20*1000; // 20 seconds
    private int countSent = 0;
    private int countSentFailed = 0;
    private int countSentSlow = 0;
    
    
    // ------------------------------------------------------------------------

    public InfluxDBJsonSender(String url) {
    	this.displayUrl = url;
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
            LOG.warn("Failed to send json to InfluxDB '" + displayUrl + "' ... rethrow ex:" + ex.getMessage());
            throw ex;
        }
    }
    
	public void sendEvent(String jsonFragment) {
		// wrap body with "[ ... ]"
		String text = "[\n" + jsonFragment + "\n]";
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
    		out.append("\n]");
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
        return "InfluxDBJsonSender[url=" + displayUrl + "]";
    }

}
