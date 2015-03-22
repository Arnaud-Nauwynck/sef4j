package org.sef4j.callstack.export.influxdb;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.sef4j.callstack.export.influxdb.jsonprinters.BasicTimeStatsLogHistogramInfluxDBPrinter;
import org.sef4j.callstack.export.influxdb.jsonprinters.PendingPerfCountInfluxDBPrinter;
import org.sef4j.callstack.export.influxdb.jsonprinters.PerfStatsInfluxDBPrinter;
import org.sef4j.callstack.stats.BasicTimeStatsLogHistogram;
import org.sef4j.callstack.stats.PendingPerfCount;
import org.sef4j.callstack.stats.PerfStats;


public class HttpPostInfluxDBJsonSenderIT extends AbstractInfluxDBSerieSenderIT {

    private HttpPostInfluxDBJsonSender sut = new HttpPostInfluxDBJsonSender(url, dbName, username, password);

    @Test
    public void testSendJSonBody() {
        // Prepare
        String json = "[ { \"name\": \"metric1\", \"columns\": [\"field1\", \"field2\", \"stringField1\" ], \"points\": [ [11.5, 20.6, \"test1\"] ] } ]";
        // Perform
        sut.sendJSonBody(json.getBytes());
        // Post-check
    }

    @Test
    public void testSendJSonBody_invalidJson() {
        // Prepare
        String json = "[ invalid json ]";
        // Perform
        try {
            sut.sendJSonBody(json.getBytes());
            Assert.fail();
        } catch(RuntimeException ex) {
            // OK
            String exMsg = ex.getMessage();
            Assert.assertEquals("Failed to POST json to 'http://localhost:8086', response code:400 msg:Bad Request", exMsg);
        }
        // Post-check
    }

    
    @Test
    public void testSend_PerfStats() {
        // Prepare
        PerfStatsInfluxDBPrinter perfStatPrinter = PerfStatsInfluxDBPrinter.DEFAULT_INSTANCE;
        PerfStats perfStats = new PerfStats();
        String perfStatsJson = perfStatPrinter.printValue("perfStats-test1", perfStats);
        // Perform
        sut.sendEvent(perfStatsJson);
        // Post-check

        // Prepare
        PerfStats perfStats2 = new PerfStats();
        String perfStatsJson2 = perfStatPrinter.printValue("perfStats-test2", perfStats2);
        // Perform
        sut.sendEvents(Arrays.asList(perfStatsJson, perfStatsJson2));
        // Post-check
    }

    @Test
    public void testSend_PendingPerfCount() {
        // Prepare
        PendingPerfCountInfluxDBPrinter pendingPerfCountPrinter = PendingPerfCountInfluxDBPrinter.INSTANCE;
        PendingPerfCount value = new PendingPerfCount();
        String json = pendingPerfCountPrinter.printValue("pending-test1", value);
        // Perform
        sut.sendEvent(json);
        // Post-check
    }


    @Test
    public void testSend_BasicTimeStatsLogHistogram() {
        // Prepare
        BasicTimeStatsLogHistogramInfluxDBPrinter basicStatsPrinter = BasicTimeStatsLogHistogramInfluxDBPrinter.INSTANCE;
        BasicTimeStatsLogHistogram value = new BasicTimeStatsLogHistogram();
        String json = basicStatsPrinter.printValue("basicTimeStats-test1", value);
        // Perform
        sut.sendEvent(json);
        // Post-check
    }

}
