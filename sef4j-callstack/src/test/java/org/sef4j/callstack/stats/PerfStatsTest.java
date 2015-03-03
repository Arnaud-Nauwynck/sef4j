package org.sef4j.callstack.stats;


import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;

public class PerfStatsTest {

	private PerfStats sut = new PerfStats();
	
	@Test
	public void testAddPending() {
		// Prepare
		long startTime1 = 123L;
		// Perform
		sut.addPending(startTime1);
		// Post-check
		Assert.assertEquals(1, sut.getPendingCount());
		Assert.assertEquals(startTime1, sut.getPendingSumStartTime());

		// Prepare
		long startTime2 = 456L;
		// Perform
		sut.addPending(startTime2);
		// Post-check
		Assert.assertEquals(2, sut.getPendingCount());
		Assert.assertEquals(startTime1 + startTime2, sut.getPendingSumStartTime());
	}
	
	@Test
	public void testRemovePending() {
		// Prepare
		long startTime1 = 123L;
		// Perform
		sut.addPending(startTime1);
		// Post-check
		Assert.assertEquals(1, sut.getPendingCount());
		Assert.assertEquals(startTime1, sut.getPendingSumStartTime());

		// Perform
		sut.removePending(startTime1);
		// Post-check
		Assert.assertEquals(0, sut.getPendingCount());
		Assert.assertEquals(0, sut.getPendingSumStartTime());

		// Prepare
		long startTime2 = 456L;
		// Perform
		sut.addPending(startTime1);
		sut.addPending(startTime2);
		sut.removePending(startTime2);
		// Post-check
		Assert.assertEquals(1, sut.getPendingCount());
		Assert.assertEquals(startTime1, sut.getPendingSumStartTime());
	}
	
	public void testIncrAndRemovePending() {
		// Prepare
		long startTime = 123, threadUserStartTime = 100, threadCpuStartTime = 80;
		long elapsedTime = 3, elapsedThreadUserTime = 2, elapsedThreadCpuTime = 1;
		long endTime = startTime + elapsedTime;
		long threadUserEndTime = threadUserStartTime + elapsedThreadUserTime;
		long threadCpuEndTime = threadUserStartTime + elapsedThreadCpuTime;
		// Perform
		sut.addPending(startTime);
		sut.incrAndRemovePending(startTime, threadUserStartTime, threadCpuStartTime, 
				endTime, threadUserEndTime, threadCpuEndTime);
		// Post-check
		Assert.assertEquals(0, sut.getPendingCount());
		Assert.assertEquals(0, sut.getPendingSumStartTime());
		BasicTimeStatsSlotInfo statElapsed1 = sut.getElapsedTimeStats().getSlotInfoCopyAt(1);
		BasicTimeStatsSlotInfo statElapsedThreadUser1 = sut.getThreadUserTimeStats().getSlotInfoCopyAt(1);
		BasicTimeStatsSlotInfo statElapsedThreadCpu1 = sut.getThreadCpuTimeStats().getSlotInfoCopyAt(1);
		Assert.assertEquals(1, statElapsed1.getCount());
		Assert.assertEquals(elapsedTime, statElapsed1.getSum());
		Assert.assertEquals(1, statElapsedThreadUser1.getCount());
		Assert.assertEquals(elapsedThreadUserTime, statElapsedThreadUser1.getSum());
		Assert.assertEquals(1, statElapsedThreadCpu1.getCount());
		Assert.assertEquals(elapsedThreadCpuTime, statElapsedThreadCpu1.getSum());
	}
	
	public void testIncr() {
		// Prepare
		long elapsedTime = 3, elapsedThreadUserTime = 2, elapsedThreadCpuTime = 1;
		// Perform
		sut.incr(elapsedTime, elapsedThreadUserTime, elapsedThreadCpuTime);
		// Post-check
		Assert.assertEquals(0, sut.getPendingCount());
		Assert.assertEquals(0, sut.getPendingSumStartTime());
		BasicTimeStatsSlotInfo statElapsed1 = sut.getElapsedTimeStats().getSlotInfoCopyAt(1);
		BasicTimeStatsSlotInfo statElapsedThreadUser1 = sut.getThreadUserTimeStats().getSlotInfoCopyAt(1);
		BasicTimeStatsSlotInfo statElapsedThreadCpu1 = sut.getThreadCpuTimeStats().getSlotInfoCopyAt(1);
		Assert.assertEquals(1, statElapsed1.getCount());
		Assert.assertEquals(elapsedTime, statElapsed1.getSum());
		Assert.assertEquals(1, statElapsedThreadUser1.getCount());
		Assert.assertEquals(elapsedThreadUserTime, statElapsedThreadUser1.getSum());
		Assert.assertEquals(1, statElapsedThreadCpu1.getCount());
		Assert.assertEquals(elapsedThreadCpuTime, statElapsedThreadCpu1.getSum());
	}

	@Test
	public void testMultithreaded() {
		// Prepare
		final int threadCount = 10;
		final int repeatCount = 1000;
		final int elapsedTime = 1, elapsedThreadUserTime = 2, elapsedThreadCpuTime = 3;
		final PerfStats stats = new PerfStats();
		final AtomicInteger remainingCount = new AtomicInteger();
		Runnable runnable = new Runnable() {
			public void run() {
				for (int i = 0; i < repeatCount; i++) {
					stats.incr(elapsedTime, elapsedThreadUserTime, elapsedThreadCpuTime);
				}
				remainingCount.decrementAndGet();
			}
		};
		// Perform
		for(int i = 0; i < threadCount; i++) {
			remainingCount.incrementAndGet();
			new Thread(runnable).start();
		}
		while(true) {
			if (0 == remainingCount.get()) {
				break;
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
		// Post-check
		BasicTimeStatsSlotInfo elapsedStats1 = stats.getElapsedTimeStats().getSlotInfoCopyAt(1);
		BasicTimeStatsSlotInfo elapsedStatsThreadUser1 = stats.getThreadUserTimeStats().getSlotInfoCopyAt(1);
		BasicTimeStatsSlotInfo elapsedStatsThreadCpu1 = stats.getThreadCpuTimeStats().getSlotInfoCopyAt(1);
		Assert.assertEquals(threadCount*repeatCount*elapsedTime, elapsedStats1.getSum());
		Assert.assertEquals(threadCount*repeatCount*elapsedThreadUserTime, elapsedStatsThreadUser1.getSum());
		Assert.assertEquals(threadCount*repeatCount*elapsedThreadCpuTime, elapsedStatsThreadCpu1.getSum());
	}
	
}
