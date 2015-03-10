package org.sef4j.callstack.export.valueprinter.helpers;

import java.io.PrintWriter;

import org.sef4j.callstack.export.valueprinter.ValuePrinter;
import org.sef4j.callstack.stats.PendingPerfCount;

/**
 * ValuePrinter for PendingPerfCount
 */
public class PendingPerfCountPrinter implements ValuePrinter<PendingPerfCount>  {

    public static final PendingPerfCountPrinter INSTANCE = new PendingPerfCountPrinter();
    
    // ------------------------------------------------------------------------

    public PendingPerfCountPrinter() {
    }
    
    // ------------------------------------------------------------------------

    @Override
    public void printValue(PrintWriter output, PendingPerfCount value) {
        int pendingCount = value.getPendingCount();
        long pendingSumStartTime = value.getPendingSumStartTime();
        output.print("pendingCount: ");
        output.print(pendingCount);
        output.print(", pendingSumStartTime: ");
        output.print(pendingSumStartTime);
    }
    
}
