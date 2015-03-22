package org.sef4j.callstack.export.influxdb.jsonprinters;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import org.junit.Assert;
import org.junit.Test;
import org.sef4j.callstack.stats.PendingPerfCount;


public class PendingPerfCountInfluxDBPrinterTest {

    private PendingPerfCountInfluxDBPrinter sut = new PendingPerfCountInfluxDBPrinter(true);
    
    @Test
    public void testPrintValue() {
        // Prepare
        ByteArrayOutputStream buffer = new ByteArrayOutputStream(); 
        PrintWriter out = new PrintWriter(buffer);
        PendingPerfCount value = new PendingPerfCount();
        // Perform
        sut.printValue(out, "metric1", value);
        out.flush();
        String res = buffer.toString();
        // Post-check
        String expected = "{ \"name\":\"metric1\",\n"
                + "\"columns\":[\"pendingCount\", \"pendingSumStartTime\"],\n"
                + "\"points\":[\n"
                + "[ 0, 0 ]\n"
                + "]}";
        Assert.assertEquals(expected, res);
    }

}
