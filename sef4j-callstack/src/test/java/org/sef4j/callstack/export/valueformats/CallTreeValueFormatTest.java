package org.sef4j.callstack.export.valueformats;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.Assert;
import org.junit.Test;
import org.sef4j.callstack.export.valueprinter.CallTreeValueWrapperPrinter;
import org.sef4j.callstack.export.valueprinter.helpers.PendingPerfCountPrinter;
import org.sef4j.callstack.stats.PendingPerfCount;
import org.sef4j.callstack.stattree.CallTreeNode;

public class CallTreeValueFormatTest {

    @Test
    public void testFormatValue() {
        // Prepare
        StringWriter buffer = new StringWriter();
        PrintWriter out = new PrintWriter(buffer);
        PendingPerfCountPrinter underlyingFmt = new PendingPerfCountPrinter();
        PendingPerfCount value = new PendingPerfCount();        
        CallTreeValueWrapperPrinter<PendingPerfCount> sut = new CallTreeValueWrapperPrinter<PendingPerfCount>(underlyingFmt, false, null, null);
        // Perform
        sut.printValue(out, CallTreeNode.newRoot(), "testProp", value);
        // Post-check
        out.flush();
        Assert.assertEquals("pendingCount: 0, pendingSumStartTime: 0", buffer.toString());
    }

    @Test
    public void testFormatValue_wrap() {
        // Prepare
        StringWriter buffer = new StringWriter();
        PrintWriter out = new PrintWriter(buffer);
        PendingPerfCountPrinter underlyingFmt = new PendingPerfCountPrinter();
        PendingPerfCount value = new PendingPerfCount();        
        CallTreeValueWrapperPrinter<PendingPerfCount> sut = new CallTreeValueWrapperPrinter<PendingPerfCount>(underlyingFmt, true, ": {", "}");
        // Perform
        sut.printValue(out, CallTreeNode.newRoot(), "testProp", value);
        // Post-check
        out.flush();
        Assert.assertEquals("testProp: {pendingCount: 0, pendingSumStartTime: 0}", buffer.toString());
    }
    
}
