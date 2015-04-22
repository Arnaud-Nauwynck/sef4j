package org.sef4j.callstack.stats.dto;

import org.sef4j.callstack.stats.PendingPerfCount;
import org.sef4j.callstack.stats.ThreadTimeUtils;
import org.sef4j.core.api.proptree.ICopySupport;

/**
 * DTO for PendingPerfCount
 * 
 */
public class PendingPerfCountDTO implements ICopySupport<PendingPerfCountDTO> {

	private int pendingCount;
	
	/**
	 * cf PendingPerfCount:
	 * sum of startTime in nanos for all currently pending threads.
	 * notice that will probably overflow long (2^64 bits), so the value is "correct modulo 2^64"
	 * 
	 * to compute average until given timeNow, use <code>(pendingCount * timeNow - pendingSumStartTime) / pendingCount</code>
	 * see getPendingAverageTimeNanosUntilTime(timeNow)
	 */
	private long pendingSumStartTime;
	
	// ------------------------------------------------------------------------

	public PendingPerfCountDTO() {
	}

	public PendingPerfCountDTO(PendingPerfCountDTO src) {
		set(src);
	}

	public PendingPerfCountDTO(PendingPerfCount src) {
		set(src);
	}

	// ------------------------------------------------------------------------

	public int getPendingCount() {
		return pendingCount;
	}

	public void setPendingCount(int p) {
		this.pendingCount = p;
	}

	public long getPendingSumStartTime() {
		return pendingSumStartTime;
	}

	public void setPendingSumStartTime(long p) {
		this.pendingSumStartTime = p;
	}
		
	@Override /* java.lang.Object */
	public PendingPerfCountDTO clone() {
		return copy();
	}
	
	@Override /* ICopySupport<> */
	public PendingPerfCountDTO copy() {
		return new PendingPerfCountDTO(this);
	}

	public void set(PendingPerfCountDTO src) {
		this.pendingCount = src.pendingCount;
		this.pendingSumStartTime = src.pendingSumStartTime;
	}

	public void set(PendingPerfCount src) {
		this.pendingCount = src.getPendingCount();
		this.pendingSumStartTime = src.getPendingSumStartTime();
	}

	public void clear() {
		this.pendingCount = 0;
		this.pendingSumStartTime = 0;
	}

	public void incr(PendingPerfCountDTO src) {
		this.pendingCount += src.pendingCount;
		this.pendingSumStartTime += src.pendingSumStartTime;
	}

	public void incr(PendingPerfCount src) {
		this.pendingCount += src.getPendingCount();
		this.pendingSumStartTime += src.getPendingSumStartTime();
	}

	// ------------------------------------------------------------------------
	
	@Override
	public String toString() {
		final int count = pendingCount;
		if (count == 0) return "PendingPerfCountsDTO[]"; 
		final long sumStart = pendingSumStartTime;

		long timeNow = ThreadTimeUtils.getTime();
		long avgNanos = (count * timeNow - sumStart) / count;
		long avgMillis = ThreadTimeUtils.nanosToMillis(avgNanos);
		
		return "PendingPerfCountsDTO[" 
				+ "count:" + count 
				+ ", sum:" + sumStart
				+ ", avgPending:" + avgMillis + " ms until now(" + timeNow + ")"
				+ "]";
	}

}
