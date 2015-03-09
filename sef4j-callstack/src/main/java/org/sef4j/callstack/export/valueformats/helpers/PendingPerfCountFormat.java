package org.sef4j.callstack.export.valueformats.helpers;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;

import org.sef4j.callstack.stats.PendingPerfCount;

/**
 * java.text.Format for converting BasicTimeStatsLogHistogram <-> String
 */
public class PendingPerfCountFormat extends Format {

    /** internal for java.io.Serializable */
    private static final long serialVersionUID = 1L;

    public static final PendingPerfCountFormat INSTANCE = new PendingPerfCountFormat();
    
    // ------------------------------------------------------------------------

    public PendingPerfCountFormat() {
    }
    
    // ------------------------------------------------------------------------

    @Override
    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
        PendingPerfCount value = (PendingPerfCount) obj;
        int pendingCount = value.getPendingCount();
        long pendingSumStartTime = value.getPendingSumStartTime();
        toAppendTo.append("pendingCount: " + pendingCount + ", pendingSumStartTime: " + pendingSumStartTime);
        return toAppendTo;
    }

    @Override
    public Object parseObject (String source, ParsePosition pos) {
        throw new UnsupportedOperationException();
    }
    
}
