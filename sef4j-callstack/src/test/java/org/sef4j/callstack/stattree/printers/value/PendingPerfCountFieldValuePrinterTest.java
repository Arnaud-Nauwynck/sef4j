package org.sef4j.callstack.stattree.printers.value;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.Assert;
import org.junit.Test;
import org.sef4j.callstack.stats.PendingPerfCount;
import org.sef4j.callstack.stattree.printers.value.PendingPerfCountFieldValuePrinter;

public class PendingPerfCountFieldValuePrinterTest {

    private PendingPerfCountFieldValuePrinter sut = new PendingPerfCountFieldValuePrinter();

    @Test
    public void testPrintValue() {
        // Prepare
        StringWriter buffer = new StringWriter();
        PrintWriter out = new PrintWriter(buffer);
        PendingPerfCount value = new PendingPerfCount();
        // Perform
        sut.printValue(out, "", value);
        // Post-check
        out.flush();
        String res = buffer.toString();
        Assert.assertEquals("pendingCount: 0, pendingSumStartTime: 0", res);
    }
    
}
