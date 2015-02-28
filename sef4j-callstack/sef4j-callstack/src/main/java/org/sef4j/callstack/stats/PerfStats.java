package org.sef4j.callstack.stats;

import java.lang.reflect.Field;

import org.sef4j.callstack.CallStackElt;

/**
 *
 */
@SuppressWarnings("restriction")
public final class PerfStats {
	
	private static final sun.misc.Unsafe UNSAFE;
	
	/*pp*/ volatile int pendingCount;
	
	/*pp*/ volatile long pendingSumStartTime;
	
	private BasicTimeStatsLogHistogram elapsedTimeStats = new BasicTimeStatsLogHistogram();
	private BasicTimeStatsLogHistogram threadUserTimeStats = new BasicTimeStatsLogHistogram();
	private BasicTimeStatsLogHistogram threadCpuTimeStats = new BasicTimeStatsLogHistogram();
	
	// ------------------------------------------------------------------------

	public PerfStats() {
	}

	// ------------------------------------------------------------------------


	public BasicTimeStatsLogHistogram getElapsedTimeStats() {
		return elapsedTimeStats;
	}

	public BasicTimeStatsLogHistogram getThreadUserTimeStats() {
		return threadUserTimeStats;
	}

	public BasicTimeStatsLogHistogram getThreadCpuTimeStats() {
		return threadCpuTimeStats;
	}

	public int getPendingCount() {
		return pendingCount; // UNSAFE.getIntVolatile(this, pendingCountFieldOffset);
	}

	public long getPendingSumStartTime() {
		return pendingSumStartTime; // UNSAFE.getLongVolatile(this, pendingSumStartTimeFieldOffset);
	}

	public void getCopyTo(PerfStats dest) {
		dest.pendingCount = getPendingCount();
		dest.pendingSumStartTime = getPendingSumStartTime();

		elapsedTimeStats.getCopyTo(dest.elapsedTimeStats);
		threadUserTimeStats.getCopyTo(dest.threadUserTimeStats);
		threadCpuTimeStats.getCopyTo(dest.threadCpuTimeStats);
	}

	public PerfStats getCopy() {
		PerfStats res = new PerfStats();
		getCopyTo(res);
		return res;
	}

	// ------------------------------------------------------------------------
	
	public void addPending(long currTime) {
		UNSAFE.getAndAddInt(this, pendingCountFieldOffset, 1); 
		UNSAFE.getAndAddLong(this, pendingSumStartTimeFieldOffset, currTime); 
	}

	public void removePending(long startedTime) {
		UNSAFE.getAndAddInt(this, pendingCountFieldOffset, -1); 
		UNSAFE.getAndAddLong(this, pendingSumStartTimeFieldOffset, - startedTime); 
	}

	public void incrAndRemovePending(long startTime, long threadUserStartTime, long threadCpuStartTime,
			long endTime, long threadUserEndTime, long threadCpuEndTime) {
		removePending(startTime);
		incr(endTime-startTime, threadUserEndTime-threadUserStartTime, threadCpuEndTime-threadCpuStartTime);
	}
	
	public void incr(long elapsedTime, long elapsedThreadUserTime, long elapsedThreadCpuTime) {
		elapsedTimeStats.incr(elapsedTime);
		threadUserTimeStats.incr(elapsedThreadUserTime);
		threadCpuTimeStats.incr(elapsedThreadCpuTime);
	}

	// Helper method using StackElt start/end times
	// ------------------------------------------------------------------------
	
	public void addPending(CallStackElt stackElt) {
		addPending(stackElt.getStartTime());
	}

	public void incrAndRemovePending(CallStackElt stackElt) {
		removePending(stackElt.getStartTime());		
		long elapsedTime = stackElt.getEndTime() - stackElt.getStartTime();
		long elapsedThreadUserTime = stackElt.getThreadUserEndTime() - stackElt.getThreadUserStartTime();
		long elapsedThreadCpuTime = stackElt.getThreadCpuEndTime() - stackElt.getThreadCpuStartTime();
		incr(elapsedTime, elapsedThreadUserTime, elapsedThreadCpuTime);
	}

	// ------------------------------------------------------------------------
	
	@Override
	public String toString() {
		return "PerfStats [" 
				+ "count:" + elapsedTimeStats.getSlotsCount()
				+ ((pendingCount != 0)? ", pending:" + pendingCount : "")
				+ ", sum ms elapsed: " + elapsedTimeStats.getSlotsSum()
				+ ", cpu:" + threadCpuTimeStats.getSlotsSum()
				+ ", user:" + threadUserTimeStats.getSlotsSum()
				+ "]";
	}
	
	
	// internal for UNSAFE
	// ------------------------------------------------------------------------
	

	private static final long pendingCountFieldOffset;
	private static final long pendingSumStartTimeFieldOffset;
	
	static {
		UNSAFE = UnsafeUtils.getUnsafe();
        
        Class<PerfStats> thisClass = PerfStats.class;
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
