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
	
	private long pendingSumStartTime;  // in millis
	private long pendingSumStartTimeNanos;
	
//	private long pendingAverageStartTimeMillis;

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
	
	public long getPendingSumStartTimeNanos() {
		return pendingSumStartTimeNanos;
	}

	public void setPendingSumStartTimeNanos(long pendingSumStartTimeNanos) {
		this.pendingSumStartTimeNanos = pendingSumStartTimeNanos;
	}

	public long getPendingAverageStartTimeMillis() {
		long res = (pendingCount != 0)? pendingSumStartTime/pendingCount : 0;
		return res;
//		return pendingAverageStartTimeMillis;
	}

//	public void setPendingAverageStartTimeMillis(long p) {
//		this.pendingAverageStartTimeMillis = p;
//	}
		
	
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
		this.pendingSumStartTimeNanos = src.pendingSumStartTimeNanos;
	}

	public void set(PendingPerfCount src) {
		this.pendingCount = src.getPendingCount();
		this.pendingSumStartTime = src.getPendingSumStartTimeMillis();
		this.pendingSumStartTimeNanos = src.getPendingSumStartTimeNanos();
	}

	public void clear() {
		this.pendingCount = 0;
		this.pendingSumStartTime = 0;
		this.pendingSumStartTimeNanos = 0;
	}

	public void incr(PendingPerfCountDTO src) {
		this.pendingCount += src.pendingCount;
		this.pendingSumStartTime += src.pendingSumStartTime;
		this.pendingSumStartTimeNanos += src.pendingSumStartTimeNanos;
	}

	public void incr(PendingPerfCount src) {
		this.pendingCount += src.getPendingCount();
		this.pendingSumStartTime += src.getPendingSumStartTimeMillis();
		this.pendingSumStartTimeNanos += src.getPendingSumStartTimeNanos();
	}

	// ------------------------------------------------------------------------
	
	@Override
	public String toString() {
		final int count = pendingCount;
		if (count == 0) return "PendingPerfCountsDTO[]"; 
		final long sum = pendingSumStartTime;
		final long avgStartTimeMillis = (count != 0)? ThreadTimeUtils.approxMillisToMillis(sum / count) : 0; 
		long avgElapsedMillis = avgStartTimeMillis - System.currentTimeMillis();
		return "PendingPerfCountsDTO[" 
				+ "count:" + count 
				+ ", sum:" + pendingSumStartTime
				+ ", sumNanos:" + pendingSumStartTimeNanos
				+ ", avgElapsed:" + avgElapsedMillis + " ms"
				+ "]";
	}

}
