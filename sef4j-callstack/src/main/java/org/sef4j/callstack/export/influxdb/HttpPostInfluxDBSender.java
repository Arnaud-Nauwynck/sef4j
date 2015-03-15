package org.sef4j.callstack.export.influxdb;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * 
 */
public class HttpPostInfluxDBSender extends InfluxDBJsonSender {

	private URL urlObj;
	
    // ------------------------------------------------------------------------
    
    public HttpPostInfluxDBSender(String url) {
        super(url);
        try {
			this.urlObj = new URL(url);
		} catch (MalformedURLException ex) {
			throw new RuntimeException("Bad url", ex);
		}
    }

    // ------------------------------------------------------------------------
    
    @Override
    public void sendJSonBody(String json) {
    	// TODO ... HttpURLConnection con = ...
    	
    }
    
}
