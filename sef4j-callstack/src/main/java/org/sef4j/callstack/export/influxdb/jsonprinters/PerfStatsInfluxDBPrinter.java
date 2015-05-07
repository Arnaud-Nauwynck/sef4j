package org.sef4j.callstack.export.influxdb.jsonprinters;

import java.io.PrintWriter;

import org.sef4j.callstack.stats.BasicTimeStatsLogHistogram;
import org.sef4j.callstack.stats.BasicTimeStatsSlotInfo;
import org.sef4j.callstack.stats.PendingPerfCount;
import org.sef4j.callstack.stats.PerfStats;
import org.sef4j.core.helpers.ext.influxdb.AbstractInfluxDBValuePrinter;

/**
 * InfluxDB serie json formater for PerfStats
 * 
 */
public class PerfStatsInfluxDBPrinter extends AbstractInfluxDBValuePrinter<PerfStats> {

    public static final PerfStatsInfluxDBPrinter ELAPSED_INSTANCE = new PerfStatsInfluxDBPrinter(true, false, true, false, false, false);
    public static final PerfStatsInfluxDBPrinter DETAILED_ELAPSED_INSTANCE = new PerfStatsInfluxDBPrinter(true, true, true, true, true, false);

    public static final PerfStatsInfluxDBPrinter DEFAULT_INSTANCE = ELAPSED_INSTANCE;

    
    private final boolean printPendings;
    private final boolean printElapsed;
    private final boolean printCpu;
    private final boolean printUser;
    private final boolean printInterleavedSlots;
    
    // ------------------------------------------------------------------------

    public PerfStatsInfluxDBPrinter(boolean printIndented, boolean printPendings, boolean printElapsed, boolean printCpu, boolean printUser, boolean printInterleavedSlots) {
        super(printIndented);
        this.printPendings = printPendings;
        this.printElapsed = printElapsed;
        this.printCpu = printCpu;
        this.printUser = printUser;
        this.printInterleavedSlots = printInterleavedSlots;
    }
    
    // ------------------------------------------------------------------------

    @Override
    public void printColumnNames(PrintWriter output) {
        if (printPendings) {
            PendingPerfCountInfluxDBPrinter.INSTANCE.printColumnNames(output);
            printSep(output);
        }
        final int lastPrintType = (printUser)? 2: (printCpu? 1 : (printElapsed? 0 : -1)); 
        final int lenMinus1 = BasicTimeStatsLogHistogram.SLOT_LEN - 1;  // until len-1  to avoid trailing ", " .. then extra step
        if (printInterleavedSlots) {
            for (int i = 0; i < lenMinus1; i++) {
                if (printElapsed) {
                    printNthElapsedColNames(output, i);
                    printSep(output);
                }
                if (printCpu) {
                    printNthCpuColNames(output, i);
                    printSep(output);
                }
                if (printUser) {
                    printNthUserColNames(output, i);
                    printSep(output);
                }
            }
            // last loop, with trailing ", "
            if (printElapsed) {
                printNthElapsedColNames(output, lenMinus1);
                printSepIfNotEq(0, lastPrintType, output);
            }
            if (printCpu) {
                printNthCpuColNames(output, lenMinus1);
                printSepIfNotEq(1, lastPrintType, output);
            }
            if (printUser) {
                printNthUserColNames(output, lenMinus1);
                printSepIfNotEq(2, lastPrintType, output);
            }
            
        } else {
            if (printElapsed) {
                for (int i = 0; i < lenMinus1; i++) {
                    printNthElapsedColNames(output, i);
                    printSep(output);
                }
                printNthElapsedColNames(output, lenMinus1);
                printSepIfNotEq(0, lastPrintType, output);
            }
            if (printCpu) {
                for (int i = 0; i < lenMinus1; i++) {
                    printNthCpuColNames(output, i);
                    printSep(output);
                }
                printNthCpuColNames(output, lenMinus1);
                printSepIfNotEq(1, lastPrintType, output);
            }
            if (printUser) {
                for (int i = 0; i < lenMinus1; i++) {
                    printNthUserColNames(output, i);
                    printSep(output);
                }
                printNthUserColNames(output, lenMinus1);
                printSepIfNotEq(2, lastPrintType, output);
            }
        }
        
    }

    @Override
    public void printPointValues(PrintWriter output, PerfStats point) {
        if (printPendings) {
            PendingPerfCount pendingCounts = point.getPendingCounts();
            PendingPerfCountInfluxDBPrinter.INSTANCE.printPointValues(output, pendingCounts);
            printSep(output);
        }
        final BasicTimeStatsSlotInfo[] timeStatsInfo = point.getElapsedTimeStats().getSlotInfoCopy();
        final BasicTimeStatsSlotInfo[] cpuStatsInfo = point.getThreadCpuTimeStats().getSlotInfoCopy();
        final BasicTimeStatsSlotInfo[] userStatsInfo = point.getThreadUserTimeStats().getSlotInfoCopy();
        final int lastPrintType = (printUser)? 2: (printCpu? 1 : (printElapsed? 0 : -1)); 
        final int lenMinus1 = BasicTimeStatsLogHistogram.SLOT_LEN - 1;  // until len-1  to avoid trailing ", " .. then extra step
        if (printInterleavedSlots) {
            for (int i = 0; i < lenMinus1; i++) {
                if (printElapsed) {
                    printNth(output, timeStatsInfo[i]);
                    printSep(output);
                }
                if (printCpu) {
                    printNth(output, cpuStatsInfo[i]);
                    printSep(output);
                }
                if (printUser) {
                    printNth(output, userStatsInfo[i]);
                    printSep(output);
                }
            }
            // last loop, with trailing ", "
            if (printElapsed) {
                printNth(output, timeStatsInfo[lenMinus1]);
                printSepIfNotEq(0, lastPrintType, output);
            }
            if (printCpu) {
                printNth(output, cpuStatsInfo[lenMinus1]);
                printSepIfNotEq(1, lastPrintType, output);
            }
            if (printUser) {
                printNth(output, userStatsInfo[lenMinus1]);
                printSepIfNotEq(2, lastPrintType, output);
            }
            
        } else {
            if (printElapsed) {
                for (int i = 0; i < lenMinus1; i++) {
                    printNth(output, timeStatsInfo[i]);
                    printSep(output);
                }
                printNth(output, timeStatsInfo[lenMinus1]);
                printSepIfNotEq(0, lastPrintType, output);
            }
            if (printCpu) {
                for (int i = 0; i < lenMinus1; i++) {
                    printNth(output, cpuStatsInfo[i]);
                    printSep(output);
                }
                printNth(output, cpuStatsInfo[lenMinus1]);
                printSepIfNotEq(1, lastPrintType, output);
            }
            if (printUser) {
                for (int i = 0; i < lenMinus1; i++) {
                    printNth(output, userStatsInfo[i]);
                    printSep(output);
                }
                printNth(output, userStatsInfo[lenMinus1]);
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
    
    private static void printNthElapsedColNames(PrintWriter output, int i) {
        BasicTimeStatsLogHistogramInfluxDBPrinter.printNthColumnNames(output, i, "count", "sum");
    }
    
    private static void printNthUserColNames(PrintWriter output, int i) {
        BasicTimeStatsLogHistogramInfluxDBPrinter.printNthColumnNames(output, i, "userCount", "userSum");
    }
    
    private static void printNthCpuColNames(PrintWriter output, int i) {
        BasicTimeStatsLogHistogramInfluxDBPrinter.printNthColumnNames(output, i, "cpuCount", "cpuSum");
    }
    
    private static void printNth(PrintWriter output, BasicTimeStatsSlotInfo slot) {
        BasicTimeStatsLogHistogramInfluxDBPrinter.printNthValue(output, slot);
    }
}
