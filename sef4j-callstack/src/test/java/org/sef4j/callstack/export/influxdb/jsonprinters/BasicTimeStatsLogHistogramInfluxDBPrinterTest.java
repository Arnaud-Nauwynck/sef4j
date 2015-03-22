package org.sef4j.callstack.export.influxdb.jsonprinters;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import org.junit.Assert;
import org.junit.Test;
import org.sef4j.callstack.stats.BasicTimeStatsLogHistogram;


public class BasicTimeStatsLogHistogramInfluxDBPrinterTest {

    private BasicTimeStatsLogHistogramInfluxDBPrinter sut = new BasicTimeStatsLogHistogramInfluxDBPrinter(true);
    
    @Test
    public void testValue() {
        // Prepare
        ByteArrayOutputStream buffer = new ByteArrayOutputStream(); 
        PrintWriter out = new PrintWriter(buffer);
        BasicTimeStatsLogHistogram value = new BasicTimeStatsLogHistogram();
        // Perform
        sut.printValue(out, "metric1", value);
        out.flush();
        String res = buffer.toString();
        // Post-check
        String expected = "{ \"name\":\"metric1\",\n"
                + "\"columns\":[\"count0\", \"sum0\", \"count1\", \"sum1\", \"count2\", \"sum2\", \"count3\", \"sum3\", \"count4\", \"sum4\", \"count5\", \"sum5\", \"count6\", \"sum6\", \"count7\", \"sum7\", \"count8\", \"sum8\", \"count9\", \"sum9\"],\n"
                + "\"points\":[\n"
                + "[ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 ]\n"
                + "]}";
        Assert.assertEquals(expected, res);
    }
}
