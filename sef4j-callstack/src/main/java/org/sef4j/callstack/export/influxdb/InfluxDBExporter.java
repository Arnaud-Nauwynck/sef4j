package org.sef4j.callstack.export.influxdb;

import java.util.List;

import org.sef4j.callstack.export.AbstractFragmentsProvidersExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class InfluxDBExporter extends AbstractFragmentsProvidersExporter<String> {

	private static final Logger LOG = LoggerFactory.getLogger(InfluxDBExporter.class);
	
    /**
     * url...mainly for display/debug message... 
     * see real connection implementation in sub-classes
     */
    private String url;
    
    
    // ------------------------------------------------------------------------

    public InfluxDBExporter(String url) {
    	super("InfluxDB:" + url);
    	this.url = url;
    }

    // ------------------------------------------------------------------------

	@Override
    public String toString() {
        return "InfluxDBExporter [url=" + url + "]";
    }
        
}
