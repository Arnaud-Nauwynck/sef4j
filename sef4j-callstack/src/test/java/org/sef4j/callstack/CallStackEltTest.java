package org.sef4j.callstack;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.sef4j.callstack.CallStackElt.StackPopper;
import org.sef4j.callstack.handlers.SinglePerfStatsUpdaterCallStackHandler;
import org.sef4j.callstack.stats.PerfStats;
import org.sef4j.callstack.stats.ThreadTimeUtils;
import org.sef4j.callstack.stats.ThreadTimeUtilsTest;

public class CallStackEltTest {

	private static final long PREC_MILLIS = 40;
	
	@Test
	public void testAddRootCallStackHandler() {
		// Prepare
		PerfStats perfStats = new PerfStats();
		CallStackPushPopHandler handler = new SinglePerfStatsUpdaterCallStackHandler(perfStats);
		CallStackPushPopHandler handler2 = new SinglePerfStatsUpdaterCallStackHandler(perfStats);
		CallStack currCallStack = LocalCallStack.get();
		CallStackElt currCallStackElt = currCallStack.curr();
		List<CallStackPushPopHandler> handlersBefore = currCallStackElt.getPushPopHandlers();
		// Perform
		currCallStackElt.addRootCallStackHandler(handler);
		// Post-check
		List<CallStackPushPopHandler> handlers2 = currCallStackElt.getPushPopHandlers();
		Assert.assertEquals(handlersBefore.size()+1, handlers2.size());
		Assert.assertSame(handler, handlers2.get(handlers2.size()-1));
	
		// Prepare
		// Perform
		currCallStackElt.addRootCallStackHandler(handler2);
		// Post-check
		List<CallStackPushPopHandler> handlers3 = currCallStackElt.getPushPopHandlers();
		Assert.assertEquals(handlersBefore.size()+2, handlers3.size());
		Assert.assertSame(handler2, handlers3.get(handlers3.size()-1));
		
		// Prepare
		// Perform
		currCallStackElt.removeRootCallStackHandler(handler);
		// Post-check
		List<CallStackPushPopHandler> handlers4 = currCallStackElt.getPushPopHandlers();
		Assert.assertEquals(handlersBefore.size()+1, handlers4.size());

		// Prepare
		// Perform
		currCallStackElt.removeRootCallStackHandler(handler2);
		// Post-check
		List<CallStackPushPopHandler> handlers5 = currCallStackElt.getPushPopHandlers();
		Assert.assertEquals(handlersBefore.size(), handlers5.size());
	}

	@Test
	public void testRemoveRootCallStackHandler() {
		// cf testAddRootCallStackHandler()
	}
	
	@Test
	public void testPushPop_sleep_perfStats() throws Exception {
		// Prepare
		PerfStats perfStats = new PerfStats();
		CallStackPushPopHandler handler = new SinglePerfStatsUpdaterCallStackHandler(perfStats);
		CallStack currCallStack = LocalCallStack.get();
		currCallStack.curr().addRootCallStackHandler(handler);
		long threadSleepMillis = 50;
		// Perform
		StackPopper toPop = LocalCallStack.meth("test").push();
		try {
			Thread.sleep(threadSleepMillis);
		} finally {
			toPop.close();
		}
		
		// Post-check
		long actualElapsedMillis = ThreadTimeUtils.approxMillisToMillis(perfStats.getElapsedTimeStats().getSlotsSum());
		ThreadTimeUtilsTest.assertApproxEquals(threadSleepMillis, 
				actualElapsedMillis,  PREC_MILLIS);
		long actualThreadUserMillis = ThreadTimeUtils.approxMillisToMillis(perfStats.getThreadUserTimeStats().getSlotsSum());
		ThreadTimeUtilsTest.assertApproxEquals(0, 
				actualThreadUserMillis,  PREC_MILLIS);
		long actualThreadCpuMillis = ThreadTimeUtils.approxMillisToMillis(perfStats.getThreadCpuTimeStats().getSlotsSum());
		ThreadTimeUtilsTest.assertApproxEquals(0, 
				actualThreadCpuMillis,  PREC_MILLIS);		
	}
	
	
	@Test
	public void testPushPop_cpu_perfStats() throws Exception {
		// Prepare
		PerfStats perfStats = new PerfStats();
		CallStackPushPopHandler handler = new SinglePerfStatsUpdaterCallStackHandler(perfStats);
		CallStack currCallStack = LocalCallStack.get();
		currCallStack.curr().addRootCallStackHandler(handler);
		
		// calibrate cpu loop for ~50 ms
		long cpuLoopMillis = 100;
		long cpuLoopCount = ThreadCpuTstUtils.cpuLoopCountForMillis(cpuLoopMillis);
		
		// Perform
		StackPopper toPop = LocalCallStack.meth("test").push();
		try {
			ThreadCpuTstUtils.cpuLoop(cpuLoopCount);
		} finally {
			toPop.close();
		}
		
		// Post-check
		long actualElapsedMillis = ThreadTimeUtils.approxMillisToMillis(perfStats.getElapsedTimeStats().getSlotsSum());
		long actualThreadUserMillis = ThreadTimeUtils.approxMillisToMillis(perfStats.getThreadUserTimeStats().getSlotsSum());
		long actualThreadCpuMillis = ThreadTimeUtils.approxMillisToMillis(perfStats.getThreadCpuTimeStats().getSlotsSum());
		long expectedLoopMillis = cpuLoopMillis + 3;
		ThreadTimeUtilsTest.assertApproxEquals(expectedLoopMillis, 
				actualElapsedMillis,  PREC_MILLIS);
		ThreadTimeUtilsTest.assertApproxEquals(expectedLoopMillis, 
				actualThreadUserMillis,  PREC_MILLIS);
		ThreadTimeUtilsTest.assertApproxEquals(expectedLoopMillis, 
				actualThreadCpuMillis,  PREC_MILLIS);
	}
}
