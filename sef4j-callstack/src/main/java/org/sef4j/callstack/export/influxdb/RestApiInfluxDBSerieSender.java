package org.sef4j.callstack.export.influxdb;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.influxdb.InfluxDB;
import org.influxdb.dto.Serie;
import org.sef4j.core.api.EventSender;

/**
 * InfluxDB Exporter to send <code>org.influxdb.dto.Serie</code>.
 * 
 * cf Rest API java proxy, with optional thirdparty maven dependency: org.influxdb:influxdb-java
 * (+ transitive jar dependencies:  retrofit, okio, httpok, guava, gson ... )  
 *
 */
public class RestApiInfluxDBSerieSender implements EventSender<Serie> {

	protected String url;
	
    /**
     * injected in ctor... 
     * to create with <code>InfluxDBFactory.connect("http://<<host>>:8086", <<username>>, <<password>>)</connect>
     * can be itself configured for http client implementation: javax or apache httpcli  
     */
    protected InfluxDB influxDB;

    protected String influxDBDatabase;

	private TimeUnit influxDBPrecision = TimeUnit.SECONDS;
    
    // ------------------------------------------------------------------------

    public RestApiInfluxDBSerieSender(String url, org.influxdb.InfluxDB influxDB, String influxDBDatabase) {
        this.url = url;
        this.influxDB = influxDB;
        this.influxDBDatabase = influxDBDatabase;
    }

    // ------------------------------------------------------------------------

    @Override
	public void sendEvent(Serie serie) {
    	influxDB.write(influxDBDatabase, influxDBPrecision, serie);
	}

	@Override
	public void sendEvents(Collection<Serie> series) {
		Serie[] serieArray = series.toArray(new Serie[series.size()]);
    	influxDB.write(influxDBDatabase, influxDBPrecision, serieArray);
	}

	// ------------------------------------------------------------------------

	@Override
	public String toString() {
		return "RestApiInfluxDBSerieSender [url=" + url + ", db=" + influxDBDatabase + "]";
	}
    	
}
