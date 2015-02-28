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

	public static long getTime() {
		return System.nanoTime();	
	}
	
	public static long getCurrentThreadCpuTime() {
		return threadMXBean.getCurrentThreadCpuTime();
	}

	public static long getCurrentThreadUserTime() {
		return threadMXBean.getCurrentThreadUserTime();
	}

	public static long nanosToApproxMillis(long nanos) {
		return nanos >>> 20;
	}

	public static long nanosToMillis(long nanos) {
		return nanos / 1000000;
	}

	public static long millisToNanos(long nanos) {
		return nanos * 1000000;
	}

	public static long approxMillisToMillis(long millis) {
		return (millis << 20) / 1000000;
	}

	public static long approxMillisToNanos(long millis) {
		return millis << 20;
	}

}
