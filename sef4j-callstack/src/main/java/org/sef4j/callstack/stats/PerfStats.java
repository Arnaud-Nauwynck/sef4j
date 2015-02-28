package org.sef4j.callstack.stats;

import org.sef4j.callstack.CallStackElt;

/**
 * class for aggregating PendingPerfCount + BasicTimeStatsLogHistogram (elapsed,threadUser,threadCpu)
 * 
 * this class is thread-safe, and lock-FREE !
 */
public final class PerfStats {
	
	private PendingPerfCount pendingCounts = new PendingPerfCount();
	
	private BasicTimeStatsLogHistogram elapsedTimeStats = new BasicTimeStatsLogHistogram();
	private BasicTimeStatsLogHistogram threadUserTimeStats = new BasicTimeStatsLogHistogram();
	private BasicTimeStatsLogHistogram threadCpuTimeStats = new BasicTimeStatsLogHistogram();
	
	// ------------------------------------------------------------------------

	public PerfStats() {
	}

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

	public void getCopyTo(PerfStats dest) {
		pendingCounts.getCopyTo(dest.pendingCounts);
		elapsedTimeStats.getCopyTo(dest.elapsedTimeStats);
		threadUserTimeStats.getCopyTo(dest.threadUserTimeStats);
		threadCpuTimeStats.getCopyTo(dest.threadCpuTimeStats);
	}

	public PerfStats getCopy() {
		PerfStats res = new PerfStats();
		getCopyTo(res);
		return res;
	}

	public void setCopy(PerfStats src) {
		src.getCopyTo(this);
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
		pendingCounts.removePending(startTime);
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
		pendingCounts.addPending(stackElt.getStartTime());
	}

	public void incrAndRemovePending(CallStackElt stackElt) {
		pendingCounts.removePending(stackElt.getStartTime());		
		long elapsedTime = stackElt.getEndTime() - stackElt.getStartTime();
		long elapsedThreadUserTime = stackElt.getThreadUserEndTime() - stackElt.getThreadUserStartTime();
		long elapsedThreadCpuTime = stackElt.getThreadCpuEndTime() - stackElt.getThreadCpuStartTime();
		incr(elapsedTime, elapsedThreadUserTime, elapsedThreadCpuTime);
	}

	// ------------------------------------------------------------------------
	
	@Override
	public String toString() {
		int pendingCount = pendingCounts.getPendingCount();
		return "PerfStats [" 
				+ ((pendingCount != 0)? ", pending:" + pendingCount : "")
				+ "count:" + elapsedTimeStats.getSlotsCount()
				+ ", sum ms elapsed: " + elapsedTimeStats.getSlotsSum()
				+ ", cpu:" + threadCpuTimeStats.getSlotsSum()
				+ ", user:" + threadUserTimeStats.getSlotsSum()
				+ "]";
	}

}
