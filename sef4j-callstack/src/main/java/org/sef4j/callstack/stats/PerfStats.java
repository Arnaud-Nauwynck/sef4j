package org.sef4j.callstack.stats;

import java.util.concurrent.Callable;

import org.sef4j.callstack.CallStackElt;
import org.sef4j.core.api.proptree.ICopySupport;
import org.sef4j.core.api.proptree.PropTreeNode;
import org.sef4j.core.api.proptree.PropTreeValueMapper.AbstractTypedPropTreeValueMapper;
import org.sef4j.core.api.proptree.PropTreeValuePredicate.AbstractTypedPropTreeValuePredicate;

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

	public static final Callable<PerfStats> FACTORY = new Callable<PerfStats>() {
        @Override
        public PerfStats call() throws Exception {
            return new PerfStats();
        }
    };

	public static final class PendingPerfCountDTOMapper extends AbstractTypedPropTreeValueMapper<PerfStats,PendingPerfCount> {
		public static final PendingPerfCountDTOMapper INSTANCE = new PendingPerfCountDTOMapper();
		public PendingPerfCount mapProp(PerfStats src) {
			return src.pendingCounts.copy();
		}
	}

	public static class MinCountPropTreeValuePredicate extends AbstractTypedPropTreeValuePredicate<PerfStats> {
		public static final MinCountPropTreeValuePredicate INSTANCE = new MinCountPropTreeValuePredicate(0);
		
		private final int minCount;
		public MinCountPropTreeValuePredicate(int minCount) {
			this.minCount = minCount;
		}

		@Override
		public boolean apply(PerfStats src) {
			return src.pendingCounts.getPendingCount() > minCount;
		}
		
	}
	
	public static final class CumulatedElapsedBasicTimeStatsLogHistogramDTOMapper 
			extends AbstractTypedPropTreeValueMapper<PerfStats,CumulatedBasicTimeStatsLogHistogramDTO> {
		public static final CumulatedElapsedBasicTimeStatsLogHistogramDTOMapper INSTANCE = new CumulatedElapsedBasicTimeStatsLogHistogramDTOMapper();
		public CumulatedBasicTimeStatsLogHistogramDTO mapProp(PerfStats src) {
			return new CumulatedBasicTimeStatsLogHistogramDTO(src.elapsedTimeStats);
		}
	}
	public static final class CumulatedThreadUserBasicTimeStatsLogHistogramDTOMapper extends AbstractTypedPropTreeValueMapper<PerfStats,CumulatedBasicTimeStatsLogHistogramDTO> {
		public static final CumulatedThreadUserBasicTimeStatsLogHistogramDTOMapper INSTANCE = new CumulatedThreadUserBasicTimeStatsLogHistogramDTOMapper();
		public CumulatedBasicTimeStatsLogHistogramDTO mapProp(PerfStats src) {
			return new CumulatedBasicTimeStatsLogHistogramDTO(src.threadUserTimeStats);
		}
	}
	public static final class CumulatedThreadCpuBasicTimeStatsLogHistogramDTOMapper extends AbstractTypedPropTreeValueMapper<PerfStats,CumulatedBasicTimeStatsLogHistogramDTO> {
		public static final CumulatedThreadCpuBasicTimeStatsLogHistogramDTOMapper INSTANCE = new CumulatedThreadCpuBasicTimeStatsLogHistogramDTOMapper();
		public CumulatedBasicTimeStatsLogHistogramDTO mapProp(PerfStats src) {
			return new CumulatedBasicTimeStatsLogHistogramDTO(src.threadCpuTimeStats);
		}
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

	public void copyTo(PerfStats dest) {
		elapsedTimeStats.copyTo(dest.elapsedTimeStats);
		threadUserTimeStats.copyTo(dest.threadUserTimeStats);
		threadCpuTimeStats.copyTo(dest.threadCpuTimeStats);

		pendingCounts.copyTo(dest.pendingCounts);
	}

	@Override /* java.lang.Object */
	public PerfStats clone() {
		return copy();
	}
	
	@Override /* ICopySupport<> */
	public PerfStats copy() {
		PerfStats res = new PerfStats();
		copyTo(res);
		return res;
	}

	public void set(PerfStats src) {
		src.copyTo(this);
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
