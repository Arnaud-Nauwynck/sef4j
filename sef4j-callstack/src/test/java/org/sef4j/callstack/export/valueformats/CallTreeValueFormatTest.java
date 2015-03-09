package org.sef4j.callstack.export.valueformats;

import org.junit.Assert;
import org.junit.Test;
import org.sef4j.callstack.export.valueformats.helpers.PendingPerfCountFormat;
import org.sef4j.callstack.stats.PendingPerfCount;
import org.sef4j.callstack.stattree.CallTreeNode;

public class CallTreeValueFormatTest {

    @Test
    public void testFormatValue() {
        // Prepare
        PendingPerfCountFormat underlyingFmt = new PendingPerfCountFormat();
        PendingPerfCount value = new PendingPerfCount();        
        FormatCallTreeValueFormatter<PendingPerfCount> sut = new FormatCallTreeValueFormatter<PendingPerfCount>(underlyingFmt, false, null, null);
        // Perform
        String res = sut.formatValue(CallTreeNode.newRoot(), "testProp", value);
        // Post-check
        Assert.assertEquals("pendingCount: 0, pendingSumStartTime: 0", res);
    }

    @Test
    public void testFormatValue_wrap() {
        // Prepare
        PendingPerfCountFormat underlyingFmt = new PendingPerfCountFormat();
        PendingPerfCount value = new PendingPerfCount();        
        FormatCallTreeValueFormatter<PendingPerfCount> sut = new FormatCallTreeValueFormatter<PendingPerfCount>(underlyingFmt, true, ": {", "}");
        // Perform
        String res = sut.formatValue(CallTreeNode.newRoot(), "testProp", value);
        // Post-check
        Assert.assertEquals("testProp: {pendingCount: 0, pendingSumStartTime: 0}", res);
    }
    
}
