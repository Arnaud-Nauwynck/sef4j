package org.sef4j.callstack.stats;

import java.lang.reflect.Field;
import java.util.concurrent.Callable;

import org.sef4j.callstack.CallStackElt;
import org.sef4j.core.api.proptree.ICopySupport;

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
	 * sum of startTime in approximate millis for all currently pending threads
	 */
	private long pendingSumStartTime;

	private long pendingSumStartTimeNanos;

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

	public long getPendingSumStartTimeNanos() {
		return UNSAFE.getLongVolatile(this, pendingSumStartTimeNanosFieldOffset);
	}

	
	public long getPendingSumStartTimeMillis() {
		long approxMillis = getPendingSumStartTime();
		return ThreadTimeUtils.approxMillisToMillis(approxMillis);
	}

	public long getPendingAverageStartTimeMillis() {
		long sum = getPendingSumStartTime();
		int count = getPendingCount();
		long avg = (count != 0)? sum/count : 0;
		return ThreadTimeUtils.approxMillisToMillis(avg);
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
		this.pendingSumStartTimeNanos = src.getPendingSumStartTimeNanos();
	}

	public void clear() {
		UNSAFE.getAndSetInt(this, pendingCountFieldOffset, 0);
		UNSAFE.getAndSetLong(this, pendingSumStartTimeFieldOffset, 0L);
		UNSAFE.getAndSetLong(this, pendingSumStartTimeNanosFieldOffset, 0L);
	}

	public void incr(PendingPerfCount src) {
		int incrCount = src.getPendingCount();
		long incrPendingSum = src.getPendingSumStartTime();
		long incrPendingSumNanos = src.getPendingSumStartTimeNanos();
		UNSAFE.getAndAddInt(this, pendingCountFieldOffset, incrCount);
		UNSAFE.getAndAddLong(this, pendingSumStartTimeFieldOffset, incrPendingSum); 
		UNSAFE.getAndAddLong(this, pendingSumStartTimeNanosFieldOffset, incrPendingSumNanos); 
	}


	// ------------------------------------------------------------------------
	@Deprecated
	public void addPending(long startTimeMillis) {
		addPending(startTimeMillis, 0);
	}
	@Deprecated
	public void removePending(long startTimeMillis) {
		removePending(startTimeMillis, 0);
	}
	
	public void addPending(long startTimeMillis, long startTimeNanos) {
		UNSAFE.getAndAddInt(this, pendingCountFieldOffset, 1); 
		UNSAFE.getAndAddLong(this, pendingSumStartTimeFieldOffset, startTimeMillis); 
		UNSAFE.getAndAddLong(this, pendingSumStartTimeNanosFieldOffset, startTimeNanos); 
	}

	public void removePending(long startTimeMillis, long startTimeNanos) {
		UNSAFE.getAndAddInt(this, pendingCountFieldOffset, -1); 
		UNSAFE.getAndAddLong(this, pendingSumStartTimeFieldOffset, -startTimeMillis); 
		UNSAFE.getAndAddLong(this, pendingSumStartTimeNanosFieldOffset, -startTimeNanos); 
	}

	// Helper method using StackElt start/end times
	// ------------------------------------------------------------------------
	
	public void addPending(CallStackElt stackElt) {
		long startTimeMillis = stackElt.getStartTimeApproxMillis();
		addPending(startTimeMillis, stackElt.getStartTime());
	}

	public void removePending(CallStackElt stackElt) {
		long startTimeMillis = stackElt.getStartTimeApproxMillis();
		removePending(startTimeMillis, stackElt.getStartTime());		
	}

	// ------------------------------------------------------------------------
	
	@Override
	public String toString() {
		final int count = pendingCount;
		if (count == 0) return "PendingPerfCounts[]";
		final long sum = this.pendingSumStartTime;
		final long sumNanos = this.pendingSumStartTimeNanos;
		final long avgMillis = (count != 0)? ThreadTimeUtils.approxMillisToMillis(sum / count) : 0; 
		
		return "PendingPerfCounts[" 
				+ "count:" + count 
				+ "sum:" + sum + ", sumNanos:" + sumNanos
				+ ", avg:" + avgMillis + " ms"
				+ "]";
	}

	// internal for UNSAFE
	// ------------------------------------------------------------------------
	
	private static final sun.misc.Unsafe UNSAFE;

	private static final long pendingCountFieldOffset;
	private static final long pendingSumStartTimeFieldOffset;
	private static final long pendingSumStartTimeNanosFieldOffset;
	
	static {
		UNSAFE = UnsafeUtils.getUnsafe();
        
        Class<PendingPerfCount> thisClass = PendingPerfCount.class;
        Field pendingCountField = getField(thisClass, "pendingCount");
        pendingCountFieldOffset = UNSAFE.objectFieldOffset(pendingCountField);

        Field pendingSumStartTimeField = getField(thisClass, "pendingSumStartTime");
        pendingSumStartTimeFieldOffset = UNSAFE.objectFieldOffset(pendingSumStartTimeField);

        Field pendingSumStartTimeNanosField = getField(thisClass, "pendingSumStartTimeNanos");
        pendingSumStartTimeNanosFieldOffset = UNSAFE.objectFieldOffset(pendingSumStartTimeNanosField);
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
