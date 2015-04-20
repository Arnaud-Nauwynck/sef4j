package org.sef4j.callstack.stats.dto;

import org.sef4j.callstack.stats.PerfStats;
import org.sef4j.core.api.proptree.ICopySupport;

/**
 * DTO class for PerfStats
 */
public final class PerfStatsDTO implements ICopySupport<PerfStatsDTO> {
	
	private PendingPerfCountDTO pendingCounts = new PendingPerfCountDTO();
	
	private CumulatedBasicTimeStatsLogHistogramDTO elapsedTimeStats = new CumulatedBasicTimeStatsLogHistogramDTO();
	private CumulatedBasicTimeStatsLogHistogramDTO threadUserTimeStats = new CumulatedBasicTimeStatsLogHistogramDTO();
	private CumulatedBasicTimeStatsLogHistogramDTO threadCpuTimeStats = new CumulatedBasicTimeStatsLogHistogramDTO();
	
	// ------------------------------------------------------------------------

	public PerfStatsDTO() {
	}

	public PerfStatsDTO(PerfStatsDTO src) {
		set(src);
	}

	public PerfStatsDTO(PerfStats src) {
		incr(src);
	}

	// ------------------------------------------------------------------------

	public PendingPerfCountDTO getPendingCounts() {
		return pendingCounts;
	}

	public CumulatedBasicTimeStatsLogHistogramDTO getElapsedTimeStats() {
		return elapsedTimeStats;
	}

	public CumulatedBasicTimeStatsLogHistogramDTO getThreadUserTimeStats() {
		return threadUserTimeStats;
	}

	public CumulatedBasicTimeStatsLogHistogramDTO getThreadCpuTimeStats() {
		return threadCpuTimeStats;
	}

	public int getPendingCount() {
		return pendingCounts.getPendingCount();
	}

	public long getPendingSumStartTime() {
		return pendingCounts.getPendingSumStartTime();
	}

	public void set(PerfStatsDTO src) {
		elapsedTimeStats.set(src.elapsedTimeStats);
		threadUserTimeStats.set(src.threadUserTimeStats);
		threadCpuTimeStats.set(src.threadCpuTimeStats);

		pendingCounts.set(src.pendingCounts);
	}

	public void incr(PerfStats src) {
		elapsedTimeStats.incr(src.getElapsedTimeStats());
		threadUserTimeStats.incr(src.getThreadUserTimeStats());
		threadCpuTimeStats.incr(src.getThreadCpuTimeStats());

		pendingCounts.incr(src.getPendingCounts());
	}


	@Override /* java.lang.Object */
	public PerfStatsDTO clone() {
		return copy();
	}
	
	@Override /* ICopySupport<> */
	public PerfStatsDTO copy() {
		return new PerfStatsDTO(this);
	}

	// ------------------------------------------------------------------------
	
	@Override
	public String toString() {
		int pendingCount = pendingCounts.getPendingCount();
		return "PerfStats [" 
				+ ((pendingCount != 0)? ", pending:" + pendingCount : "")
				+ "count:" + elapsedTimeStats.totalCount()
				+ ", sum ms elapsed: " + elapsedTimeStats.totalSum()
				+ ", cpu:" + threadCpuTimeStats.totalSum()
				+ ", user:" + threadUserTimeStats.totalSum()
				+ "]";
	}

}
