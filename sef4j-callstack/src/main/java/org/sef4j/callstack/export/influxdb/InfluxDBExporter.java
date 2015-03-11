package org.sef4j.callstack.export.influxdb;

public abstract class InfluxDBExporter {

    /**
     * url...mainly for display/debug message... 
     * see real connection implementation in sub-classes
     */
    private String url;

    // ------------------------------------------------------------------------

    public InfluxDBExporter(String url) {
        this.url = url;
    }

    // ------------------------------------------------------------------------

    
    @Override
    public String toString() {
        return "InfluxDBExporter [url=" + url + "]";
    }
        
}
