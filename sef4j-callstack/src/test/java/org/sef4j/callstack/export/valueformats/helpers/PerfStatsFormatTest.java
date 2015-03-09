package org.sef4j.callstack.export.valueformats.helpers;

import org.junit.Assert;
import org.junit.Test;
import org.sef4j.callstack.stats.PerfStats;

public class PerfStatsFormatTest {

    @Test
    public void testFormat() {
        // Prepare
        PerfStatsFormat sut = new PerfStatsFormat(true, true, true, true, true);
        PerfStats value = new PerfStats();
        // Perform
        String res = sut.format(value);
        // Post-check
        Assert.assertNotNull(res);
        Assert.assertEquals("pendingCount: 0, pendingSumStartTime: 0, "
                + "count0: 0, sum0: 0, cpuCount0: 0, cpuSum0: 0, userCount0: 0, userSum0: 0, " 
                + "count1: 0, sum1: 0, cpuCount1: 0, cpuSum1: 0, userCount1: 0, userSum1: 0, " 
                + "count2: 0, sum2: 0, cpuCount2: 0, cpuSum2: 0, userCount2: 0, userSum2: 0, " 
                + "count3: 0, sum3: 0, cpuCount3: 0, cpuSum3: 0, userCount3: 0, userSum3: 0, " 
                + "count4: 0, sum4: 0, cpuCount4: 0, cpuSum4: 0, userCount4: 0, userSum4: 0, " 
                + "count5: 0, sum5: 0, cpuCount5: 0, cpuSum5: 0, userCount5: 0, userSum5: 0, " 
                + "count6: 0, sum6: 0, cpuCount6: 0, cpuSum6: 0, userCount6: 0, userSum6: 0, " 
                + "count7: 0, sum7: 0, cpuCount7: 0, cpuSum7: 0, userCount7: 0, userSum7: 0, " 
                + "count8: 0, sum8: 0, cpuCount8: 0, cpuSum8: 0, userCount8: 0, userSum8: 0, " 
                + "count9: 0, sum9: 0, cpuCount9: 0, cpuSum9: 0, userCount9: 0, userSum9: 0", res);
    }
    
    
    @Test
    public void testFormat_nonInterleaved() {
        // Prepare
        PerfStatsFormat sut = new PerfStatsFormat(false, true, true, true, false);
        PerfStats value = new PerfStats();
        // Perform
        String res = sut.format(value);
        // Post-check
        Assert.assertNotNull(res);
        Assert.assertEquals("count0: 0, sum0: 0, count1: 0, sum1: 0, count2: 0, sum2: 0, count3: 0, sum3: 0, count4: 0, sum4: 0, " 
                + "count5: 0, sum5: 0, count6: 0, sum6: 0, count7: 0, sum7: 0, count8: 0, sum8: 0, count9: 0, sum9: 0, " 
                + "cpuCount0: 0, cpuSum0: 0, cpuCount1: 0, cpuSum1: 0, cpuCount2: 0, cpuSum2: 0, cpuCount3: 0, cpuSum3: 0, cpuCount4: 0, cpuSum4: 0, " 
                + "cpuCount5: 0, cpuSum5: 0, cpuCount6: 0, cpuSum6: 0, cpuCount7: 0, cpuSum7: 0, cpuCount8: 0, cpuSum8: 0, cpuCount9: 0, cpuSum9: 0, " 
                + "userCount0: 0, userSum0: 0, userCount1: 0, userSum1: 0, userCount2: 0, userSum2: 0, userCount3: 0, userSum3: 0, userCount4: 0, userSum4: 0, " 
                + "userCount5: 0, userSum5: 0, userCount6: 0, userSum6: 0, userCount7: 0, userSum7: 0, userCount8: 0, userSum8: 0, userCount9: 0, userSum9: 0", res);
    }
}
