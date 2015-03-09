package org.sef4j.callstack.export.valueformats.helpers;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;

import org.sef4j.callstack.stats.BasicTimeStatsLogHistogram;
import org.sef4j.callstack.stats.BasicTimeStatsSlotInfo;

/**
 * java.text.Format for converting BasicTimeStatsLogHistogram <-> String
 */
public class BasicTimeStatsLogHistogramFormat extends Format {

    /** internal for java.io.Serializable */
    private static final long serialVersionUID = 1L;

    public static final BasicTimeStatsLogHistogramFormat INSTANCE = new BasicTimeStatsLogHistogramFormat();
    
    // ------------------------------------------------------------------------
    
    public BasicTimeStatsLogHistogramFormat() {
    }

    // ------------------------------------------------------------------------

    @Override
    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
        BasicTimeStatsLogHistogram value = (BasicTimeStatsLogHistogram) obj;
        BasicTimeStatsSlotInfo[] timeStatsInfo = value.getSlotInfoCopy();
        final int len = timeStatsInfo.length;
        for (int i = 0; i < len; i++) {
            toAppendTo.append("count" + i + ": " + timeStatsInfo[i].getCount() + ", sum" + i + ": " + timeStatsInfo[i].getSum() + ", ");
        }
        toAppendTo.delete(toAppendTo.length() - 2, toAppendTo.length()); // remove trailing ", "
        return toAppendTo;
    }

    @Override
    public Object parseObject (String source, ParsePosition pos) {
        throw new UnsupportedOperationException();
    }
    
}
