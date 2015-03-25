package org.sef4j.callstack.stattree.printers.value;

import java.io.PrintWriter;
import java.util.List;

import org.sef4j.callstack.stats.PendingPerfCount;
import org.sef4j.core.helpers.exporters.ValuePrinter;

/**
 * ValuePrinter for PendingPerfCount
 * <BR/>
 * print as  
 * <PRE>pendingCount: 123, pendingSumStartTime: 456</PRE>
 */
public class PendingPerfCountFieldValuePrinter implements ValuePrinter<PendingPerfCount>  {

    public static final PendingPerfCountFieldValuePrinter INSTANCE = new PendingPerfCountFieldValuePrinter();
    
    // ------------------------------------------------------------------------

    public PendingPerfCountFieldValuePrinter() {
    }
    
    // ------------------------------------------------------------------------

    public void printValues(PrintWriter output, String name, List<PendingPerfCount> values) {
        for(PendingPerfCount value : values) {
            printValue(output, name, value);
        }
    }
    
    @Override
    public void printValue(PrintWriter output, String name, PendingPerfCount value) {
        int pendingCount = value.getPendingCount();
        long pendingSumStartTime = value.getPendingSumStartTime();
        output.print("pendingCount: ");
        output.print(pendingCount);
        output.print(", pendingSumStartTime: ");
        output.print(pendingSumStartTime);
    }
    
}
