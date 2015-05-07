package org.sef4j.callstack.stats;

import java.lang.reflect.Field;
import java.util.concurrent.Callable;

import org.sef4j.callstack.CallStackElt;
import org.sef4j.core.util.ICopySupport;

/**
 * perf counter for pending thread in a section
 * 
 * this class is multi-thread safe, and lock-FREE!
 */
@SuppressWarnings("restriction")
public class PendingPerfCount implements ICopySupport<PendingPerfCount> {

	/**
	 * count of currently pending threads
	 */
	private int pendingCount;

	/**
	 * sum of startTime in nanos for all currently pending threads.
	 * notice that will probably overflow long (2^64 bits), so the value is "correct modulo 2^64"
	 * 
	 * to compute average until given timeNow, use <code>(pendingCount * timeNow - pendingSumStartTime) / pendingCount</code>
	 * see getPendingAverageTimeNanosUntilTime(timeNow)
	 */
	private long pendingSumStartTime;

	// ------------------------------------------------------------------------

	public PendingPerfCount() {
	}

	public PendingPerfCount(PendingPerfCount src) {
		set(src);
	}

	public static final Callable<PendingPerfCount> FACTORY = new Callable<PendingPerfCount>() {
		@Override
		public PendingPerfCount call() throws Exception {
			return new PendingPerfCount();
		}
	};

	// ------------------------------------------------------------------------

	public int getPendingCount() {
		return UNSAFE.getIntVolatile(this, pendingCountFieldOffset);
	}

	public long getPendingSumStartTime() {
		return UNSAFE.getLongVolatile(this, pendingSumStartTimeFieldOffset);
	}

	public long getPendingAverageTimeNanosUntilTime(long timeNanos) {
		int count = getPendingCount();
		if (count == 0) return 0;
		long sumStart = getPendingSumStartTime();
		long avg = (count * timeNanos - sumStart) / count;
		return avg;
	}

	public long getPendingAverageTimeMillisUntilTime(long timeNanos) {
		long avgNanos = getPendingAverageTimeNanosUntilTime(timeNanos);
		return ThreadTimeUtils.nanosToMillis(avgNanos);
	}
	
	@Override /* java.lang.Object */
	public PendingPerfCount clone() {
		return copy();
	}
	
	@Override /* ICopySupport<> */
	public PendingPerfCount copy() {
		return new PendingPerfCount(this);
	}

	public void set(PendingPerfCount src) {
		this.pendingCount = src.getPendingCount();
		this.pendingSumStartTime = src.getPendingSumStartTime();
	}

	public void clear() {
		UNSAFE.getAndSetInt(this, pendingCountFieldOffset, 0);
		UNSAFE.getAndSetLong(this, pendingSumStartTimeFieldOffset, 0L);
	}

	public void incr(PendingPerfCount src) {
		int incrCount = src.getPendingCount();
		long incrPendingSum = src.getPendingSumStartTime();
		UNSAFE.getAndAddInt(this, pendingCountFieldOffset, incrCount);
		UNSAFE.getAndAddLong(this, pendingSumStartTimeFieldOffset, incrPendingSum); 
	}


	// ------------------------------------------------------------------------
	
	public void addPending(long startTimeMillis) {
		UNSAFE.getAndAddInt(this, pendingCountFieldOffset, 1); 
		UNSAFE.getAndAddLong(this, pendingSumStartTimeFieldOffset, startTimeMillis); 
	}

	public void removePending(long startTimeMillis) {
		UNSAFE.getAndAddInt(this, pendingCountFieldOffset, -1); 
		UNSAFE.getAndAddLong(this, pendingSumStartTimeFieldOffset, -startTimeMillis);
	}

	// Helper method using StackElt start/end times
	// ------------------------------------------------------------------------
	
	public void addPending(CallStackElt stackElt) {
		addPending(stackElt.getStartTime());
	}

	public void removePending(CallStackElt stackElt) {
		removePending(stackElt.getStartTime());		
	}

	// ------------------------------------------------------------------------
	
	@Override
	public String toString() {
		final int count = pendingCount;
		if (count == 0) return "PendingPerfCounts[]";
		final long sum = this.pendingSumStartTime;
		
		long timeNow = ThreadTimeUtils.getTime();
		long avgMillisUntilNow = getPendingAverageTimeMillisUntilTime(timeNow);
		
		return "PendingPerfCounts[" 
				+ "count:" + count + "sum:" + sum 
				+ ", avgMillis:" + avgMillisUntilNow + " ms until now:" + timeNow
				+ "]";
	}

	// internal for UNSAFE
	// ------------------------------------------------------------------------
	
	private static final sun.misc.Unsafe UNSAFE;

	private static final long pendingCountFieldOffset;
	private static final long pendingSumStartTimeFieldOffset;
	
	static {
		UNSAFE = UnsafeUtils.getUnsafe();
        
        Class<PendingPerfCount> thisClass = PendingPerfCount.class;
        Field pendingCountField = getField(thisClass, "pendingCount");
        pendingCountFieldOffset = UNSAFE.objectFieldOffset(pendingCountField);

        Field pendingSumStartTimeField = getField(thisClass, "pendingSumStartTime");
        pendingSumStartTimeFieldOffset = UNSAFE.objectFieldOffset(pendingSumStartTimeField);
    }
	
	private static Field getField(Class<?> clss, String name) {
		Field[] fields = clss.getDeclaredFields();
		for(Field f : fields) {
			if (f.getName().equals(name)) {
				return f;
			}
		}
		return null;
	}
	
}
