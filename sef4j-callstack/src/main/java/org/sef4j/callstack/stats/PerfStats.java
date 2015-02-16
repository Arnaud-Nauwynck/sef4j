package org.sef4j.callstack.stats;

import java.lang.reflect.Field;

/**
 *
 */
@SuppressWarnings("restriction")
public class PerfStats {

	private static final sun.misc.Unsafe UNSAFE;
	
	private volatile int pendingCount;
	
	private volatile int pendingSumStartTime;
	
	private PerfTimeStats elapsedTimeStats = new PerfTimeStats();
	private PerfTimeStats userTimeStats = new PerfTimeStats();
	private PerfTimeStats cpuTimeStats = new PerfTimeStats();
	
	// ------------------------------------------------------------------------

	public PerfStats() {
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
	
	public int getPendingCount() {
		return pendingCount; // UNSAFE.getIntVolatile(this, pendingCountFieldOffset);
	}

	public long getPendingSumStartTime() {
		return pendingSumStartTime; // UNSAFE.getLongVolatile(this, pendingSumStartTimeFieldOffset);
	}

	
	
	// internal 
	// ------------------------------------------------------------------------
	
	private static final long pendingCountFieldOffset;
	private static final long pendingSumStartTimeFieldOffset;
	
	static {
		UNSAFE = sun.misc.Unsafe.getUnsafe();
        
        Class<PerfStats> thisClass = PerfStats.class;
        Field pendingCountField;
        try {
        	pendingCountField = thisClass.getField("pendingCount");
		} catch (Exception e) {
			throw new IllegalStateException();
		}
        pendingCountFieldOffset = UNSAFE.objectFieldOffset(pendingCountField);

        Field pendingSumStartTimeField;
        try {
        	pendingSumStartTimeField = thisClass.getField("pendingSumStartTimeField");
		} catch (Exception e) {
			throw new IllegalStateException();
		}
        pendingSumStartTimeFieldOffset = UNSAFE.objectFieldOffset(pendingSumStartTimeField);
        		
    }

}
