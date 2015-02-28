package org.sef4j.callstack.handlers;

import org.sef4j.callstack.CallStackElt;
import org.sef4j.callstack.CallStackPushPopHandler;
import org.sef4j.callstack.stats.PerfStats;
import org.sef4j.callstack.stats.ThreadTimeUtils;

/**
 * CallStackPushPopHandler to update PerfStats for a single level of push()/pop()
 */
public class SinglePerfStatsUpdaterCallStackHandler extends CallStackPushPopHandler {
	
	private final PerfStats perfStats;
	private int level;
	
	public SinglePerfStatsUpdaterCallStackHandler(PerfStats perfStats) {
		this.perfStats = perfStats;
	}

	@Override
	public void onPush(CallStackElt stackElt) {
		if (level == 0) {
			stackElt.onPushAddCallStackPushPopHandler(this);
			level++;
		}
	}

	@Override
	public void onPop(CallStackElt stackElt) {
		if (level == 1) {
			level--;
			long elapsedTime = ThreadTimeUtils.nanosToApproxMillis(
					stackElt.getEndTime() - stackElt.getStartTime());
			long elapsedThreadUserTime = ThreadTimeUtils.nanosToApproxMillis(
					stackElt.getThreadUserEndTime() - stackElt.getThreadUserStartTime());
			long elapsedThreadCpuTime = ThreadTimeUtils.nanosToApproxMillis(
					stackElt.getThreadCpuEndTime() - stackElt.getThreadCpuStartTime());
			perfStats.incr(elapsedTime, elapsedThreadUserTime, elapsedThreadCpuTime);
		}
	}

	@Override
	public void onProgressStep(CallStackElt stackElt) {
		// do nothing
	}

}