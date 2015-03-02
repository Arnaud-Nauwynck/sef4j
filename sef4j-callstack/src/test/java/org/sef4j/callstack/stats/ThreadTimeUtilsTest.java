package org.sef4j.callstack.stats;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

import org.junit.Assert;
import org.junit.Test;
import org.sef4j.callstack.ThreadCpuTstUtils;


public class ThreadTimeUtilsTest {

	private static final long prec = 30 * 1000*1000; // precision ~30ms, in nanos
	
	@Test
	public void testCheckThreadCpuEnabled() {
		// Prepare
		ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
		threadMXBean.setThreadCpuTimeEnabled(false);
		// Perform
		ThreadTimeUtils.checkThreadCpuEnabled();
		// Post-check
		Assert.assertTrue(!threadMXBean.isThreadCpuTimeSupported() || threadMXBean.isThreadCpuTimeEnabled());
	}

	@Test
	public void testGetTime() throws InterruptedException {
		// Prepare
		long sleepTime = 50;
		// Perform
		long time1 = ThreadTimeUtils.getTime();
		Thread.sleep(sleepTime);
		long time2 = ThreadTimeUtils.getTime();
		// Post-check
		Assert.assertTrue(time1 <= time2);
		assertApproxEquals(time1 + sleepTime, time2, 2*prec);
	}

	@Test
	public void testGetCurrentThreadCpuTime_sleep() throws InterruptedException {
		// Prepare
		long sleepTime = 30;
		// Perform
		long time1 = ThreadTimeUtils.getCurrentThreadCpuTime();
		Thread.sleep(sleepTime);
		long time2 = ThreadTimeUtils.getCurrentThreadCpuTime();		
		// Post-check
		Assert.assertTrue(time1 <= time2);
		assertApproxEquals(time1, time2, prec);
	}

	@Test
	public void testGetCurrentThreadCpuTime_cpu() throws InterruptedException {
		// Prepare
		long expectedCpuTime = 80;
		long cpuLoop = ThreadCpuTstUtils.cpuLoopCountForMillis(expectedCpuTime);
		// Perform
		long time1 = ThreadTimeUtils.getCurrentThreadCpuTime();
		ThreadCpuTstUtils.cpuLoop(cpuLoop);
		long time2 = ThreadTimeUtils.getCurrentThreadCpuTime();		
		// Post-check
		Assert.assertTrue(time1 <= time2);
		long expectedTime2 = time1 + ThreadTimeUtils.millisToNanos(expectedCpuTime);
		assertApproxEquals(expectedTime2, time2, prec);
	}

	public static void assertApproxEquals(long expectedValue, long actualValue, long prec) {
		if (! (expectedValue - prec <= actualValue)) {
			Assert.fail("expected " + expectedValue + " ~ " + actualValue + " +/-" + prec + " .. got diff:" + (actualValue-expectedValue));
		}
		if (! (actualValue <= expectedValue + prec)) {
			Assert.fail("expected " + expectedValue + " ~ " + actualValue + " +/-" + prec + " .. got diff:" + (actualValue-expectedValue));
		}
	}
	
}
