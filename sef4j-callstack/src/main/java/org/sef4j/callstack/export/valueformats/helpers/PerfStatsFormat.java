package org.sef4j.callstack.export.valueformats.helpers;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;

import org.sef4j.callstack.stats.BasicTimeStatsSlotInfo;
import org.sef4j.callstack.stats.PendingPerfCount;
import org.sef4j.callstack.stats.PerfStats;

/**
 * java.text.Format for converting BasicTimeStatsLogHistogram <-> String
 */
public class PerfStatsFormat extends Format {

    /** internal for java.io.Serializable */
    private static final long serialVersionUID = 1L;

    public static final PerfStatsFormat ELAPSED_INSTANCE = new PerfStatsFormat(false, true, false, false, false);
    public static final PerfStatsFormat DETAILED_ELAPSED_INSTANCE = new PerfStatsFormat(true, true, true, true, false);

    public static final PerfStatsFormat DEFAULT_INSTANCE = ELAPSED_INSTANCE;

    
    private final boolean printPendings;
    private final boolean printElapsed;
    private final boolean printCpu;
    private final boolean printUser;
    private final boolean printInterleavedSlots;
    
    // ------------------------------------------------------------------------

    public PerfStatsFormat(boolean printPendings, boolean printElapsed, boolean printCpu, boolean printUser, boolean printInterleavedSlots) {
        super();
        this.printPendings = printPendings;
        this.printElapsed = printElapsed;
        this.printCpu = printCpu;
        this.printUser = printUser;
        this.printInterleavedSlots = printInterleavedSlots;
    }
    
    // ------------------------------------------------------------------------

    @Override
    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
        PerfStats value = (PerfStats) obj;
        if (printPendings) {
            PendingPerfCount pendingCounts = value.getPendingCounts();
            int pendingCount = pendingCounts.getPendingCount();
            long pendingSumStartTime = pendingCounts.getPendingSumStartTime();
            toAppendTo.append("pendingCount: " + pendingCount + ", pendingSumStartTime: " + pendingSumStartTime + ", ");
        }
        BasicTimeStatsSlotInfo[] timeStatsInfo = value.getElapsedTimeStats().getSlotInfoCopy();
        BasicTimeStatsSlotInfo[] cpuStatsInfo = value.getThreadCpuTimeStats().getSlotInfoCopy();
        BasicTimeStatsSlotInfo[] userStatsInfo = value.getThreadUserTimeStats().getSlotInfoCopy();
        final int len = timeStatsInfo.length;
        if (printInterleavedSlots) {
            for (int i = 0; i < len; i++) {
                if (printElapsed) {
                    toAppendTo.append("count" + i + ": " + timeStatsInfo[i].getCount() + ", sum" + i + ": " + timeStatsInfo[i].getSum() + ", ");
                }
                if (printCpu) {
                    toAppendTo.append("cpuCount" + i + ": " + cpuStatsInfo[i].getCount() + ", cpuSum" + i + ": " + cpuStatsInfo[i].getSum() + ", ");
                }
                if (printUser) {
                    toAppendTo.append("userCount" + i + ": " + userStatsInfo[i].getCount() + ", userSum" + i + ": " + userStatsInfo[i].getSum() + ", ");
                }
            }
        } else {
            if (printElapsed) {
                for (int i = 0; i < len; i++) {
                    toAppendTo.append("count" + i + ": " + timeStatsInfo[i].getCount() + ", sum" + i + ": " + timeStatsInfo[i].getSum() + ", ");
                }
            }
            if (printCpu) {
                for (int i = 0; i < len; i++) {
                    toAppendTo.append("cpuCount" + i + ": " + cpuStatsInfo[i].getCount() + ", cpuSum" + i + ": " + cpuStatsInfo[i].getSum() + ", ");
                }
            }
            if (printUser) {
                for (int i = 0; i < len; i++) {
                    toAppendTo.append("userCount" + i + ": " + userStatsInfo[i].getCount() + ", userSum" + i + ": " + userStatsInfo[i].getSum() + ", ");
                }
            }
        }
        toAppendTo.delete(toAppendTo.length() - 2, toAppendTo.length()); // remove trailing ", "
        return toAppendTo;
    }

    @Override
    public Object parseObject (String source, ParsePosition pos) {
        throw new UnsupportedOperationException();
    }
    
}
