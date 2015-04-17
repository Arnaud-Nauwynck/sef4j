package org.sef4j.callstack.stats;

import java.util.concurrent.Callable;
import java.util.function.Predicate;

import org.sef4j.callstack.CallStackElt;
import org.sef4j.callstack.stats.BasicTimeStatsLogHistogram.MinCountPropTreeValuePredicate;
import org.sef4j.core.api.proptree.ICopySupport;

/**
 * class for aggregating PendingPerfCount + BasicTimeStatsLogHistogram (elapsed,threadUser,threadCpu)
 * 
 * this class is thread-safe, and lock-FREE !
 */
public final class PerfStats implements ICopySupport<PerfStats> {
	
	private PendingPerfCount pendingCounts = new PendingPerfCount();
	
	private BasicTimeStatsLogHistogram elapsedTimeStats = new BasicTimeStatsLogHistogram();
	private BasicTimeStatsLogHistogram threadUserTimeStats = new BasicTimeStatsLogHistogram();
	private BasicTimeStatsLogHistogram threadCpuTimeStats = new BasicTimeStatsLogHistogram();
	
	// ------------------------------------------------------------------------

	public PerfStats() {
	}

	public PerfStats(PerfStats src) {
		set(src);
	}

	public static final Callable<PerfStats> FACTORY = new Callable<PerfStats>() {
        @Override
        public PerfStats call() throws Exception {
            return new PerfStats();
        }
    };
	
	// ------------------------------------------------------------------------

	public PendingPerfCount getPendingCounts() {
		return pendingCounts;
	}

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
		return pendingCounts.getPendingCount();
	}

	public long getPendingSumStartTime() {
		return pendingCounts.getPendingSumStartTime();
	}

	@Override /* java.lang.Object */
	public PerfStats clone() {
		return copy();
	}
	
	@Override /* ICopySupport<> */
	public PerfStats copy() {
		return new PerfStats(this);
	}

	public void set(PerfStats src) {
		this.elapsedTimeStats.set(src.elapsedTimeStats);
		threadUserTimeStats.set(src.threadUserTimeStats);
		threadCpuTimeStats.set(src.threadCpuTimeStats);

		pendingCounts.set(src.pendingCounts);
	}

	// ------------------------------------------------------------------------
	
	public void addPending(long currTime) {
		pendingCounts.addPending(currTime);
	}

	public void removePending(long startedTime) {
		pendingCounts.removePending(startedTime);
	}

	public void incrAndRemovePending(long startTime, long threadUserStartTime, long threadCpuStartTime,
			long endTime, long threadUserEndTime, long threadCpuEndTime) {
		incr(endTime-startTime, threadUserEndTime-threadUserStartTime, threadCpuEndTime-threadCpuStartTime);
		pendingCounts.removePending(startTime);
	}
	
	public void incr(long elapsedTime, long elapsedThreadUserTime, long elapsedThreadCpuTime) {
		elapsedTimeStats.incr(elapsedTime);
		threadUserTimeStats.incr(elapsedThreadUserTime);
		threadCpuTimeStats.incr(elapsedThreadCpuTime);
	}

	// Helper method using StackElt start/end times
	// ------------------------------------------------------------------------
	
	public void addPending(CallStackElt stackElt) {
		pendingCounts.addPending(stackElt.getStartTime());
	}

	public void incrAndRemovePending(CallStackElt stackElt) {
		long elapsedTime = stackElt.getEndTime() - stackElt.getStartTime();
		long elapsedThreadUserTime = stackElt.getThreadUserEndTime() - stackElt.getThreadUserStartTime();
		long elapsedThreadCpuTime = stackElt.getThreadCpuEndTime() - stackElt.getThreadCpuStartTime();
		incr(elapsedTime, elapsedThreadUserTime, elapsedThreadCpuTime);

		pendingCounts.removePending(stackElt.getStartTime());		
	}

	// ------------------------------------------------------------------------
	
	@Override
	public String toString() {
		int pendingCount = pendingCounts.getPendingCount();
		return "PerfStats [" 
				+ ((pendingCount != 0)? ", pending:" + pendingCount : "")
				+ "count:" + elapsedTimeStats.cumulatedCount()
				+ ", sum ms elapsed: " + elapsedTimeStats.cumulatedSum()
				+ ", cpu:" + threadCpuTimeStats.cumulatedSum()
				+ ", user:" + threadUserTimeStats.cumulatedSum()
				+ "]";
	}

}
