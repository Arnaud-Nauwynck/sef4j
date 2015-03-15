package org.sef4j.callstack.export.valueprinter.helpers;

import java.io.PrintWriter;

import org.sef4j.callstack.stats.BasicTimeStatsLogHistogram;
import org.sef4j.callstack.stats.BasicTimeStatsSlotInfo;
import org.sef4j.core.helpers.exporters.ValuePrinter;

/**
 * ValuePrinter for BasicTimeStatsLogHistogram
 * 
 * print as
 * <PRE>count0: 123, sum0: 2345, count1: 12, sum1:  ....    count9: 123, sum9:123456</PRE>
 */
public class BasicTimeStatsLogHistogramPrinter implements ValuePrinter<BasicTimeStatsLogHistogram> {
    
    public static final BasicTimeStatsLogHistogramPrinter INSTANCE = new BasicTimeStatsLogHistogramPrinter();
    
    // ------------------------------------------------------------------------
    
    public BasicTimeStatsLogHistogramPrinter() {
    }

    // ------------------------------------------------------------------------

    @Override
    public void printValue(PrintWriter output, BasicTimeStatsLogHistogram value){
        BasicTimeStatsSlotInfo[] timeStatsInfo = value.getSlotInfoCopy();
        final int lenMinus1 = timeStatsInfo.length - 1;
        for (int i = 0; i < lenMinus1; i++) {
            printNth(output, timeStatsInfo[i], "count", "sum", i);
            output.print(", ");
        }
        printNth(output, timeStatsInfo[lenMinus1], "count", "sum", lenMinus1);
        // no trailing ", "
    }
    
    public static void printNth(PrintWriter output, BasicTimeStatsSlotInfo statsInfo, String countStatName, String sumStatName, int i) {
        output.print(countStatName);
        output.print(i);
        output.print(": ");
        output.print(statsInfo.getCount());
        output.print(", ");
        
        output.print(sumStatName);
        output.print(i);
        output.print(": ");
        output.print(statsInfo.getSum());
    }

}
