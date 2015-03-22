package org.sef4j.callstack.export.valueformats.helpers;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.Assert;
import org.junit.Test;
import org.sef4j.callstack.export.valueprinter.helpers.PendingPerfCountFieldValuePrinter;
import org.sef4j.callstack.stats.PendingPerfCount;

public class PendingPerfCountFormatTest {

    @Test
    public void testFormat() {
        // Prepare
        StringWriter buffer = new StringWriter();
        PrintWriter out = new PrintWriter(buffer);
        PendingPerfCountFieldValuePrinter sut = new PendingPerfCountFieldValuePrinter();
        PendingPerfCount value = new PendingPerfCount();
        // Perform
        sut.printNthValue(out, value);
        // Post-check
        out.flush();
        String res = buffer.toString();
        Assert.assertEquals("pendingCount: 0, pendingSumStartTime: 0", res);
    }
    
}
