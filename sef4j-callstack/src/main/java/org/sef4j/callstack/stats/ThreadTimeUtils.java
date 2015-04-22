package org.sef4j.callstack.stats;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadTimeUtils {

	private static final Logger LOG = LoggerFactory.getLogger(ThreadTimeUtils.class);
	
	private static final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
	static {
		try {
			checkThreadCpuEnabled();
		} catch(Exception ex) {
			LOG.error("FAILED to check/activate threadCpuTime! ... ignore, no rethrow!", ex);
			// ignore, no rethrow!
		}
	}
	
	public static void checkThreadCpuEnabled() {
		if (!threadMXBean.isThreadCpuTimeEnabled()) {
			boolean isSupported = threadMXBean.isThreadCpuTimeSupported();
			if (isSupported) {
				LOG.info("ThreadMXBean.isThreadCpuTimeEnabled(): false => enable");
				threadMXBean.setThreadCpuTimeEnabled(true);
			} else {
				LOG.warn("ThreadMXBean.isThreadCpuTimeSupported(): false !!");
			}
		}
	}

	private static long TIME_OFFSET = System.nanoTime();
	
	public static long getTime() {
		return System.nanoTime() - TIME_OFFSET;	
	}
	
	public static long getCurrentThreadCpuTime() {
		return threadMXBean.getCurrentThreadCpuTime();
	}

	public static long getCurrentThreadUserTime() {
		return threadMXBean.getCurrentThreadUserTime();
	}

	private static final long MILLIS_TO_NANOS = 1000000;
	private static final long APPROX_1M_BIT_SHIFT = 20;

	public static long nanosToApproxMillis(long nanos) {
		return nanos >> 20;
				// nanos >>> 20;  //??
	}

	public static long nanosToMillis(long nanos) {
		return nanos / MILLIS_TO_NANOS;
	}

	public static long millisToNanos(long nanos) {
		return nanos * MILLIS_TO_NANOS;
	}

	public static long approxMillisToMillis(long millis) {
		return // (millis / MILLIS_TO_NANOS) << 20; // may truncate to 0 
				(millis << 20) / MILLIS_TO_NANOS; // ... may overflow?? 
				// (((millis << 10) / 1000) << 10) / 1000) 
	}

	public static long approxMillisToNanos(long millis) {
		return millis << 20;
	}

}
