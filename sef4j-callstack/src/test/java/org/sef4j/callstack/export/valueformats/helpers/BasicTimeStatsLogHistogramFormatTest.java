package org.sef4j.callstack.export.valueformats.helpers;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.Assert;
import org.junit.Test;
import org.sef4j.callstack.export.valueprinter.helpers.BasicTimeStatsLogHistogramFieldValuePrinter;
import org.sef4j.callstack.stats.BasicTimeStatsLogHistogram;

public class BasicTimeStatsLogHistogramFormatTest {

    @Test
    public void testFormat() {
        // Prepare
        StringWriter buffer = new StringWriter();
        PrintWriter out = new PrintWriter(buffer);
        BasicTimeStatsLogHistogramFieldValuePrinter sut = new BasicTimeStatsLogHistogramFieldValuePrinter();
        BasicTimeStatsLogHistogram value = new BasicTimeStatsLogHistogram();
        // Perform
        sut.printNthValue(out, value);
        // Post-check
        out.flush();
        String res = buffer.toString();
        Assert.assertEquals("count0: 0, sum0: 0, count1: 0, sum1: 0, count2: 0, sum2: 0, count3: 0, sum3: 0, count4: 0, sum4: 0, " 
                + "count5: 0, sum5: 0, count6: 0, sum6: 0, count7: 0, sum7: 0, count8: 0, sum8: 0, count9: 0, sum9: 0", res);
    }
}
