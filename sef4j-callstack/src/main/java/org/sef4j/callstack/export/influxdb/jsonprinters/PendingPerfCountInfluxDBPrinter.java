package org.sef4j.callstack.export.influxdb.jsonprinters;

import java.io.PrintWriter;

import org.sef4j.callstack.stats.PendingPerfCount;
import org.sef4j.core.helpers.exporters.influxdb.AbstractInfluxDBValuePrinter;

/**
 * InfluxDB serie json row value formater for PendingPerfCount
 * <BR/>
 * print as  <PRE>123, 1234 </PRE>
 * <PRE>
 * { 
 *   "name": "metric1", 
 *   "columns": { "pendingCount", "pendingSumStartTime" }
 *   "values": [
 *      { 123, 1234 }
 *   ]
 * }</PRE>
 */
public class PendingPerfCountInfluxDBPrinter extends AbstractInfluxDBValuePrinter<PendingPerfCount>  {

    public static final PendingPerfCountInfluxDBPrinter INSTANCE = new PendingPerfCountInfluxDBPrinter(true);
    
    // ------------------------------------------------------------------------

    public PendingPerfCountInfluxDBPrinter(boolean printIndented) {
        super(printIndented);
    }
    
    // ------------------------------------------------------------------------

    @Override
    public void printColumnNames(PrintWriter output) {
        output.print("\"pendingCount\", \"pendingSumStartTime\"");
    }

    @Override
    public void printPointValues(PrintWriter output, PendingPerfCount point) {
        int pendingCount = point.getPendingCount();
        long pendingSumStartTime = point.getPendingSumStartTime();
        output.print(pendingCount);
        output.print(", ");
        output.print(pendingSumStartTime);
    }

}
