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
	
	private long pendingSumStartTime;  // converted from nanos to millis
	
//	private long pendingAverageStartTimeMillis;

	// ------------------------------------------------------------------------

	public PendingPerfCountDTO() {
	}

	public PendingPerfCountDTO(PendingPerfCountDTO src) {
		set(src);
	}

	public PendingPerfCountDTO(PendingPerfCount src) {
		incr(src);
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

	public long getPendingAverageStartTimeMillis() {
		return (pendingCount != 0)? pendingSumStartTime/pendingCount : 0;
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
//		this.pendingAverageStartTimeMillis = src.pendingAverageStartTimeMillis;
	}

	public void clear() {
		this.pendingCount = 0;
		this.pendingSumStartTime = 0;
//		this.pendingAverageStartTimeMillis = 0;
	}

	public void incr(PendingPerfCountDTO src) {
		this.pendingCount += src.pendingCount;
		this.pendingSumStartTime += src.pendingSumStartTime;
//		this.pendingAverageStartTimeMillis = (pendingCount != 0)? pendingSumStartTime/pendingCount : 0;
	}

	public void incr(PendingPerfCount src) {
		this.pendingCount += src.getPendingCount();
		this.pendingSumStartTime += ThreadTimeUtils.nanosToMillis(src.getPendingSumStartTime());
//		this.pendingAverageStartTimeMillis = (pendingCount != 0)? pendingSumStartTime/pendingCount : 0;
	}

	// ------------------------------------------------------------------------
	
//	public void addPending(long startTime) {
//		this.pendingCount++; 
//		this.pendingSumStartTime += startTime; 
//	}
//
//	public void removePending(long startTime) {
//		this.pendingCount--; 
//		this.pendingSumStartTime -= startTime; 
//	}

	// ------------------------------------------------------------------------
	
	@Override
	public String toString() {
		final int count = pendingCount;
		final long sum = pendingSumStartTime;
		final long avgStartTimeMillis = (count != 0)? sum / count : 0; 
		long avgElapsedMillis = avgStartTimeMillis - System.currentTimeMillis();
		return "PendingPerfCountsDTO[" 
				+ ((count != 0)? "count:" + count + ", avgElapsed:" + avgElapsedMillis + " ms": "")
				+ "]";
	}

}
