package org.sef4j.callstack.stattree.printers.value;

import java.io.PrintWriter;
import java.util.List;

import org.sef4j.callstack.stats.BasicTimeStatsLogHistogram;
import org.sef4j.callstack.stats.BasicTimeStatsSlotInfo;
import org.sef4j.core.helpers.exporters.ValuePrinter;

/**
 * ValuePrinter for BasicTimeStatsLogHistogram
 * <BR/>
 * print as
 * <PRE>count0: 123, sum0: 2345, count1: 12, sum1:  ....    count9: 123, sum9:123456</PRE>
 */
public class BasicTimeStatsLogHistogramFieldValuePrinter implements ValuePrinter<BasicTimeStatsLogHistogram> {
    
    public static final BasicTimeStatsLogHistogramFieldValuePrinter INSTANCE = new BasicTimeStatsLogHistogramFieldValuePrinter();
    
    // ------------------------------------------------------------------------
    
    public BasicTimeStatsLogHistogramFieldValuePrinter() {
    }

    // ------------------------------------------------------------------------

    public void printValues(PrintWriter output, String name, List<BasicTimeStatsLogHistogram> values) {
        for(BasicTimeStatsLogHistogram value : values) {
            printValue(output, name, value);
        }
    }
    
    @Override
    public void printValue(PrintWriter output, String name, BasicTimeStatsLogHistogram value){
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
