package org.sef4j.callstack.stats;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

import org.junit.Assert;
import org.junit.Test;
import org.sef4j.callstack.ThreadCpuTstUtils;


public class ThreadTimeUtilsTest {

	private static final long prec = 40 * 1000*1000; // precision ~40ms, in nanos
	
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

	/**
	 * compute diff between successive call to getTime()
	 * => interpret as precision + speed of loop 
	 * display as statistical histogram :
	 * <PRE>
diff	count
15	0.003701
16	0.056233
17	0.201642
18	0.445661
19	0.185245
20	0.036909
21	0.034543
22	0.01548
23	0.005571
24	0.003046
25	0.002344
	 * </PRE>
	 * 
	 * Got asymmetric "gaussian" curve, centered at ~18 nanos
	 * in range from ~15 to ~30 nanos + seldom values above (gc, thread swap, ..) 
	 * consecutive GetTime() are at least 15 nanos diff
	 * <PRE>
	 * count
	 *  ^        .
	 *  |        
	 *  |       . .
	 *  |       .  .
	 *  |      .     .   
	 *  +------------------------> diff
	 *        15      30
	 *           18
	 * </PRE>
	 * 
	 */
	@Test
	public void testGetTime_precision() throws Exception {
		int maxDiff = 100000;
		int repeat = 1000000;
		doReapeatAccumulateTimeDiff(maxDiff, repeat); // first call fro hostspot
		int[] countPerDiff = doReapeatAccumulateTimeDiff(maxDiff, repeat);
		StringBuilder sb = new StringBuilder();
		StringBuilder csvSb = new StringBuilder();
		csvSb.append("diff,count\n");
		for(int i = 0; i < maxDiff; i++) {
			if (countPerDiff[i] != 0) {
				sb.append(i + ":" + countPerDiff[i] + ", ");
				if (countPerDiff[i] > 2) { // apply small threshold...
					double proba = ((double)countPerDiff[i])/repeat;
					csvSb.append(i + "," + proba + "\n");
				}
			}
		}
		// FileUtils.write(new File("target/precision-GetTime.csv"), csvSb.toString());
		System.out.println("histogram for precision diff of consecutive getTime() in nanos:" + sb.toString());
	}
	
	private static int[] doReapeatAccumulateTimeDiff(int maxDiff, int repeat) {
		int[] countPerDiff = new int[maxDiff];
		long prevTime = ThreadTimeUtils.getTime();
		for (int i = 0; i < repeat; i++) {
			long time = ThreadTimeUtils.getTime();
			long diff = time - prevTime;
			if (0 < diff && diff < maxDiff-1) {
				countPerDiff[(int)diff]++;
			} else if (diff >= maxDiff){
				countPerDiff[maxDiff-1]++;
			} else if (diff < 0) {
				// should not occur!
			} else {
				// out of range.. ignore...
			}
			prevTime = time;
		}
		return countPerDiff;
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

	private static final int CST_M = 1000*1000;
	private static final int CST_APPROX_M = 1024*1024;
	private static final long MILLIS_TO_NANOS = 1000000;
	private static final long CST_ANY = 40000;

	@Test
	public void testBitShiftApproxMillisToNanos() {
		Assert.assertEquals(1 << 20, CST_APPROX_M);
	}

	@Test
	public void testNanosToApproxMillisToMillis() {
		Assert.assertEquals(CST_ANY-2, //round...
				ThreadTimeUtils.approxMillisToMillis(ThreadTimeUtils.nanosToApproxMillis(CST_ANY * MILLIS_TO_NANOS)));
		Assert.assertEquals(2*CST_ANY-1, //round...
				ThreadTimeUtils.approxMillisToMillis(ThreadTimeUtils.nanosToApproxMillis(2*CST_ANY * MILLIS_TO_NANOS)));
		Assert.assertEquals(3*CST_ANY-1, //round...
				ThreadTimeUtils.approxMillisToMillis(ThreadTimeUtils.nanosToApproxMillis(3*CST_ANY * MILLIS_TO_NANOS)));
	}

	@Test
	public void testNanosToApproxMillis() {
		Assert.assertEquals(CST_M, ThreadTimeUtils.nanosToApproxMillis(CST_APPROX_M * MILLIS_TO_NANOS));
		Assert.assertEquals(1, ThreadTimeUtils.nanosToApproxMillis(CST_APPROX_M));
	}

	@Test
	public void testNanosToMillis() {
		Assert.assertEquals(1, ThreadTimeUtils.nanosToMillis(MILLIS_TO_NANOS));
	}

	@Test
	public void testMillisToNanos() {
		Assert.assertEquals(MILLIS_TO_NANOS, ThreadTimeUtils.millisToNanos(1));
	}

	@Test
	public void testApproxMillisToMillis() {
		long approxMillis = ThreadTimeUtils.nanosToApproxMillis(CST_ANY * MILLIS_TO_NANOS);
		Assert.assertEquals(CST_ANY-2, //approx 
				ThreadTimeUtils.approxMillisToMillis(approxMillis));
		//?? Assert.assertEquals(CST_ANY*CST_M, ThreadTimeUtils.approxMillisToMillis(CST_ANY*CST_APPROX_M));
	}

	@Test
	public void testApproxMillisToNanos() {
		// Assert.assertEquals(CST_M * MILLIS_TO_NANOS, ThreadTimeUtils.approxMillisToNanos(CST_APPROX_M));
		Assert.assertEquals(CST_APPROX_M, ThreadTimeUtils.approxMillisToNanos(1));
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
