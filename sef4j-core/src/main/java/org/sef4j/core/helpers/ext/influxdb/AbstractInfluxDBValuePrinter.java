package org.sef4j.core.helpers.ext.influxdb;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import org.sef4j.core.helpers.proptree.printers.ValuePrinter;

/**
 * abstract helper class for InfluxDB serie json formater
 * 
 * see sub-classes for concrete series per type: 
 * - PerfStats => PerfStatsInfluxDBPrinter
 * - PendingPerfCount => PendingPerfCountInfluxDBPrinter
 * - BasicTimeStatsLogHistogram => BasicTimeStatsLogHistogramInfluxDBPrinter
 * 
 * 
 * <PRE>                                        +-------------+
 *           printValue(serieName, value)       |             |                 JSon text
 *              ---------------------------->   |             |    ---------->   "{ "name": "<<metricName>>",
 *                                              +-------------+                     "columns": [ "<<col1>>", ...<<columnNames>> ],
 *                                                     ^                            "points": [ { <<points1>> }, ....{ <<points>> } ]
 *                                                     |                          }"
 *    PerfStats                             +----------+----------+
 *      PendingPerfCount                    |          |          |
 *        BasicTimeStatsLogHistogram     +-----+    +-----+     +-----+ 
 *                                       |     |    |     |     |     | 
 *                                       +-----+    +-----+     +-----+
 * </PRE>  
 * 
 * Example of JSon formatting
 * <PRE>
 * { 
 *   "name": "metric1", 
 *   "columns": [ "count0", "sum0", "count1", "sum1" .... "count9", "sum9" ],
 *   "points":  [ 
 *      [ 12, 3456, 123, 4563,  ...  123, 456546 ]
 *   ]
 * }</PRE>
 * 
 */
public abstract class AbstractInfluxDBValuePrinter<T> implements ValuePrinter<T> {

    private boolean printIndented;
    
    // ------------------------------------------------------------------------

    public AbstractInfluxDBValuePrinter(boolean printIndented) {
        this.printIndented = printIndented;
    }
    
    // ------------------------------------------------------------------------

    /** helper for printValues(PrintWriter output, ...) */
    public String printValues(String metricName, List<T> values) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream(); 
        PrintWriter out = new PrintWriter(buffer);
        printValues(out, metricName, values);
        out.flush();
        return buffer.toString();
    }

    /** helper for printValue(PrintWriter output, ...) */
    public String printValue(String metricName, T value) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream(); 
        PrintWriter out = new PrintWriter(buffer);
        printValue(out, metricName, value);
        out.flush();
        return buffer.toString();
    }
    

    
    @Override
    public final void printValues(PrintWriter output, String metricName, List<T> values) {
        printMetricHeader(output, metricName);
        
        for(Iterator<T> iter = values.iterator(); iter.hasNext(); ) {
            T value = iter.next();
            output.print("[ "); 
            printPointValues(output, value);
            output.print(" ]"); 
            if (iter.hasNext()) {
                output.print(", ");
            }
            optPrintln(output);
        }
        
        printMetricFooter(output);
    }

    @Override
    public final void printValue(PrintWriter output, String metricName, T value) {
        printMetricHeader(output, metricName);
        
        output.print("[ "); 
        printPointValues(output, value);
        output.print(" ]");
        
        printMetricFooter(output);
    }


    private void printMetricHeader(PrintWriter output, String metricName) {
        output.print("{ \"name\":\"");
        output.print(metricName);
        output.print("\",");
        optPrintln(output);
        
        output.print("\"columns\":[");
        printColumnNames(output);
        output.print("],");
        optPrintln(output);
        
        output.print("\"points\":["); 
        optPrintln(output);
    }

    private void printMetricFooter(PrintWriter output) {
        optPrintln(output);
        output.print("]");
        // optPrintln(output);
        output.print("}");
    }

    protected void optPrintln(PrintWriter output) {
        if (printIndented) {
            output.print("\n");
        } else {
            output.print(" ");
        }
    }
    
    public abstract void printColumnNames(PrintWriter output);

    public abstract void printPointValues(PrintWriter output, T point);
    
}
