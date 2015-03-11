package org.sef4j.callstack.export.influxdb;

import org.influxdb.InfluxDB;

/**
 * InfluxDB Exporter using  
 * cf Rest API java proxy, with optional thirdparty maven dependency: org.influxdb:influxdb-java
 * (+ transitive jar dependencies:  retrofit, okio, httpok, guava, gson ... )  
 *
 */
public class RestApiInfluxDBExporter extends InfluxDBExporter {

    /**
     * injected in ctor... 
     * to create with <code>InfluxDBFactory.connect("http://<<host>>:8086", <<username>>, <<password>>)</connect>
     * can be itself configured for http client implementation: javax or apache httpcli  
     */
    protected InfluxDB influxDB;

    // ------------------------------------------------------------------------

    public RestApiInfluxDBExporter(String url, org.influxdb.InfluxDB influxDB) {
        super(url);
        this.influxDB = influxDB;
    }

    // ------------------------------------------------------------------------

    
}
