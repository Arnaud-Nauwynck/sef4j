package org.sef4j.callstack.export.valueformats.helpers;

import org.junit.Assert;
import org.junit.Test;
import org.sef4j.callstack.stats.PendingPerfCount;

public class PendingPerfCountFormatTest {

    @Test
    public void testFormat() {
        // Prepare
        PendingPerfCountFormat sut = new PendingPerfCountFormat();
        PendingPerfCount value = new PendingPerfCount();
        // Perform
        String res = sut.format(value);
        // Post-check
        Assert.assertNotNull(res);
        Assert.assertEquals("pendingCount: 0, pendingSumStartTime: 0", res);
    }
    
}
