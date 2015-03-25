package org.sef4j.callstack;

import org.sef4j.callstack.stats.ThreadTimeUtilsTest;

public class ThreadCpuTstUtils {

	static {
		cpuLoop(10000); // hot compile..
		sleep(10);
		cpuLoop(10000); // hot compile..
		sleep(10);
	}
	
//	public static final long cpuLoopCountFor100Millis = cpuLoopCountForMillis(100);

	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch(InterruptedException ex) {
			// ignore, no rethrow!
		}
	}
	
	public static void cpuLoop(long cpuLoopCount) {
		for (int i = 0; i < cpuLoopCount; i++) {
			int dbg = i; if (dbg%2 == 0) dbg++; dbg *= 2;
		}
	}

	public static void sleepAndCpu(long sleepMillis, long threadCpuLoop) {
		if (sleepMillis > 0) {
			sleep(sleepMillis);
		}
		if (threadCpuLoop > 0) {
			cpuLoop(threadCpuLoop);
		}
	}
	
	public static long cpuLoopCountForMillis(long threadCpuMillis) {		
		long cpuLoopCalibrate = 40000000;
		long startTime = System.nanoTime();
		cpuLoop(cpuLoopCalibrate);
		long cpuTimeCalibrateMillis = (System.nanoTime() - startTime) / 1000000;
		long cpuLoop = cpuLoopCalibrate * threadCpuMillis / cpuTimeCalibrateMillis;

		// check
		long checkStartTime = System.nanoTime();
		cpuLoop(cpuLoop);
		long checkCpuTime = (System.nanoTime() - checkStartTime) / 1000000;
		ThreadTimeUtilsTest.assertApproxEquals(threadCpuMillis, checkCpuTime, 60); // precision 60 ms ???
		
		return cpuLoop;
	}

	
}
