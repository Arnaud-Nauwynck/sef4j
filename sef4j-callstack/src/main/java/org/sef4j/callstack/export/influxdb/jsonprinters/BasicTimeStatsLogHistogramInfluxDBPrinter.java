package org.sef4j.callstack.export.influxdb.jsonprinters;

import java.io.PrintWriter;

import org.sef4j.callstack.stats.BasicTimeStatsLogHistogram;
import org.sef4j.callstack.stats.BasicTimeStatsSlotInfo;

/**
 * InfluxDB serie json row value formater for BasicTimeStatsLogHistogram
 * <PRE>
 * { 
 *   "name": "metric1", 
 *   "columns":[ "count0", "sum0", "count1", "sum1" .... "count9", "sum9" ],
 *   "points":[ 
 *      [ 12, 3456, 123, 4563,  ...  123, 456546 ]
 *   ]
 * }</PRE>
 */
public class BasicTimeStatsLogHistogramInfluxDBPrinter extends AbstractInfluxDBValuePrinter<BasicTimeStatsLogHistogram> {
    
    public static final BasicTimeStatsLogHistogramInfluxDBPrinter INSTANCE = new BasicTimeStatsLogHistogramInfluxDBPrinter(false);
    
    // ------------------------------------------------------------------------
    
    public BasicTimeStatsLogHistogramInfluxDBPrinter(boolean printIndented) {
        super(printIndented);
    }

    // ------------------------------------------------------------------------

    @Override
    public void printColumnNames(PrintWriter output) {
        printColumnNames(output, "count", "sum");
    }

    @Override
    public void printPointValues(PrintWriter output, BasicTimeStatsLogHistogram point) {
        BasicTimeStatsSlotInfo[] timeStatsInfo = point.getSlotInfoCopy();
        final int lenMinus1 = BasicTimeStatsLogHistogram.SLOT_LEN - 1;
        for (int i = 0; i < lenMinus1; i++) {
            printNthValue(output, timeStatsInfo[i]);
            output.print(", ");
        }
        printNthValue(output, timeStatsInfo[lenMinus1]); // no trailing ", "
    }
    
    
    public static void printColumnNames(PrintWriter output, String countName, String sumName) {
        final int lenMinus1 = BasicTimeStatsLogHistogram.SLOT_LEN - 1;
        for (int i = 0; i < lenMinus1; i++) {
            printNthColumnNames(output, i, countName, sumName);
            output.print(", ");
        }
        printNthColumnNames(output, lenMinus1, countName, sumName); // no trailing ", "
    }
    
    public static void printNthColumnNames(PrintWriter output, int i, String countName, String sumName) {
        output.print("\"" + countName + i + "\", \"" + sumName + i + "\"");
    }

    public static void printNthValue(PrintWriter output, BasicTimeStatsSlotInfo slot) {
        output.print(slot.getCount());
        output.print(", ");
        output.print(slot.getSum());
    }
    
}
