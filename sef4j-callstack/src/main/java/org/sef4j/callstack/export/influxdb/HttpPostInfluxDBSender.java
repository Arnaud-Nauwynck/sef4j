package org.sef4j.callstack.export.influxdb;

/**
 * 
 */
public class HttpPostInfluxDBSender extends InfluxDBSender {

    // ------------------------------------------------------------------------
    
    public HttpPostInfluxDBSender(String url) {
        super(url);
    }

    // ------------------------------------------------------------------------
    
    @Override
    public void sendJSonBody(String json) {
    	// TODO Auto-generated method stub
    	
    }
    
}
