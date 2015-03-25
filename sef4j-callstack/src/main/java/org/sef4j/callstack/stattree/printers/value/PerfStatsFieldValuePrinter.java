package org.sef4j.callstack.stattree.printers.value;

import java.io.PrintWriter;
import java.util.List;

import org.sef4j.callstack.stats.BasicTimeStatsSlotInfo;
import org.sef4j.callstack.stats.PendingPerfCount;
import org.sef4j.callstack.stats.PerfStats;
import org.sef4j.core.helpers.exporters.ValuePrinter;

/**
 * ValuePrinter for PerfStats
 */
public class PerfStatsFieldValuePrinter implements ValuePrinter<PerfStats> {

    public static final PerfStatsFieldValuePrinter ELAPSED_INSTANCE = new PerfStatsFieldValuePrinter(false, true, false, false, false);
    public static final PerfStatsFieldValuePrinter DETAILED_ELAPSED_INSTANCE = new PerfStatsFieldValuePrinter(true, true, true, true, false);

    public static final PerfStatsFieldValuePrinter DEFAULT_INSTANCE = ELAPSED_INSTANCE;

    
    private final boolean printPendings;
    private final boolean printElapsed;
    private final boolean printCpu;
    private final boolean printUser;
    private final boolean printInterleavedSlots;
    
    // ------------------------------------------------------------------------

    public PerfStatsFieldValuePrinter(boolean printPendings, boolean printElapsed, boolean printCpu, boolean printUser, boolean printInterleavedSlots) {
        super();
        this.printPendings = printPendings;
        this.printElapsed = printElapsed;
        this.printCpu = printCpu;
        this.printUser = printUser;
        this.printInterleavedSlots = printInterleavedSlots;
    }
    
    // ------------------------------------------------------------------------

    public void printValues(PrintWriter output, String name, List<PerfStats> values) {
        for(PerfStats value : values) {
            printValue(output, name, value);
        }
    }
    
    @Override
    public void printValue(PrintWriter output, String propName, PerfStats value) {
        if (printPendings) {
            PendingPerfCount pendingCounts = value.getPendingCounts();
            PendingPerfCountFieldValuePrinter.INSTANCE.printValue(output, propName, pendingCounts);
            printSep(output);
        }
        final BasicTimeStatsSlotInfo[] timeStatsInfo = value.getElapsedTimeStats().getSlotInfoCopy();
        final BasicTimeStatsSlotInfo[] cpuStatsInfo = value.getThreadCpuTimeStats().getSlotInfoCopy();
        final BasicTimeStatsSlotInfo[] userStatsInfo = value.getThreadUserTimeStats().getSlotInfoCopy();
        final int lastPrintType = (printUser)? 2: (printCpu? 1 : (printElapsed? 0 : -1)); 
        final int lenMinus1 = timeStatsInfo.length - 1;  // until len-1  to avoid trailing ", " .. then extra step
        if (printInterleavedSlots) {
            for (int i = 0; i < lenMinus1; i++) {
                if (printElapsed) {
                    printNthElapsed(output, timeStatsInfo[i], i);
                    printSep(output);
                }
                if (printCpu) {
                    printNthCpu(output, cpuStatsInfo[i], i);
                    printSep(output);
                }
                if (printUser) {
                    printNthUser(output, userStatsInfo[i], i);
                    printSep(output);
                }
            }
            // last loop, with trailing ", "
            if (printElapsed) {
                printNthElapsed(output, timeStatsInfo[lenMinus1], lenMinus1);
                printSepIfNotEq(0, lastPrintType, output);
            }
            if (printCpu) {
                printNthCpu(output, cpuStatsInfo[lenMinus1], lenMinus1);
                printSepIfNotEq(1, lastPrintType, output);
            }
            if (printUser) {
                printNthUser(output, userStatsInfo[lenMinus1], lenMinus1);
                printSepIfNotEq(2, lastPrintType, output);
            }
            
        } else {
            if (printElapsed) {
                for (int i = 0; i < lenMinus1; i++) {
                    printNthElapsed(output, timeStatsInfo[i], i);
                    printSep(output);
                }
                printNthElapsed(output, timeStatsInfo[lenMinus1], lenMinus1);
                printSepIfNotEq(0, lastPrintType, output);
            }
            if (printCpu) {
                for (int i = 0; i < lenMinus1; i++) {
                    printNthCpu(output, cpuStatsInfo[i], i);
                    printSep(output);
                }
                printNthCpu(output, cpuStatsInfo[lenMinus1], lenMinus1);
                printSepIfNotEq(1, lastPrintType, output);
            }
            if (printUser) {
                for (int i = 0; i < lenMinus1; i++) {
                    printNthUser(output, userStatsInfo[i], i);
                    printSep(output);
                }
                printNthUser(output, userStatsInfo[lenMinus1], lenMinus1);
                printSepIfNotEq(2, lastPrintType, output);
            }
        }
    }

    private static void printSep(PrintWriter output) {
        output.print(", ");
    }
    
    private static void printSepIfNotEq(int expected, int actual, PrintWriter output) {
        if (expected != actual) {
            output.print(", ");
        }
    }
    
    private static void printNthElapsed(PrintWriter output, BasicTimeStatsSlotInfo statsInfo, int i) {
        BasicTimeStatsLogHistogramFieldValuePrinter.printNth(output, statsInfo, "count", "sum", i);
    }
    
    private static void printNthUser(PrintWriter output, BasicTimeStatsSlotInfo statsInfo, int i) {
        BasicTimeStatsLogHistogramFieldValuePrinter.printNth(output, statsInfo, "userCount", "userSum", i);
    }
    
    private static void printNthCpu(PrintWriter output, BasicTimeStatsSlotInfo statsInfo, int i) {
        BasicTimeStatsLogHistogramFieldValuePrinter.printNth(output, statsInfo, "cpuCount", "cpuSum", i);
    }
    
    
}
