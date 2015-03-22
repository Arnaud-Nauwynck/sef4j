package org.sef4j.callstack.export.influxdb.jsonprinters;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import org.junit.Assert;
import org.junit.Test;
import org.sef4j.callstack.stats.PerfStats;


public class PerfStatsInfluxDBPrinterTest {


    private PerfStatsInfluxDBPrinter sut = new PerfStatsInfluxDBPrinter(true, true, true, true, true, true);

    @Test
    public void testPrintValue() {
        // Prepare
        ByteArrayOutputStream buffer = new ByteArrayOutputStream(); 
        PrintWriter out = new PrintWriter(buffer);
        PerfStats value = new PerfStats();
        // Perform
        sut.printValue(out, "metric1", value);
        out.flush();
        String res = buffer.toString();
        // Post-check
        String expected = "{ \"name\":\"metric1\",\n"
                + "\"columns\":[\"pendingCount\", \"pendingSumStartTime\", " 
                + "\"count0\", \"sum0\", \"cpuCount0\", \"cpuSum0\", \"userCount0\", \"userSum0\", " 
                + "\"count1\", \"sum1\", \"cpuCount1\", \"cpuSum1\", \"userCount1\", \"userSum1\", " 
                + "\"count2\", \"sum2\", \"cpuCount2\", \"cpuSum2\", \"userCount2\", \"userSum2\", " 
                + "\"count3\", \"sum3\", \"cpuCount3\", \"cpuSum3\", \"userCount3\", \"userSum3\", " 
                + "\"count4\", \"sum4\", \"cpuCount4\", \"cpuSum4\", \"userCount4\", \"userSum4\", " 
                + "\"count5\", \"sum5\", \"cpuCount5\", \"cpuSum5\", \"userCount5\", \"userSum5\", " 
                + "\"count6\", \"sum6\", \"cpuCount6\", \"cpuSum6\", \"userCount6\", \"userSum6\", " 
                + "\"count7\", \"sum7\", \"cpuCount7\", \"cpuSum7\", \"userCount7\", \"userSum7\", " 
                + "\"count8\", \"sum8\", \"cpuCount8\", \"cpuSum8\", \"userCount8\", \"userSum8\", " 
                + "\"count9\", \"sum9\", \"cpuCount9\", \"cpuSum9\", \"userCount9\", \"userSum9\"],\n"
                + "\"points\":[\n"
                + "[ 0, 0, "
                + "0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, " 
                + "0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 ]\n"
                + "]}";
        Assert.assertEquals(expected, res);
    }
}
