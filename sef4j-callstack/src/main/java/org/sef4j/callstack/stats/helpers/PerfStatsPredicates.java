package org.sef4j.callstack.stats.helpers;

import java.util.function.Predicate;

import org.sef4j.callstack.stats.BasicTimeStatsLogHistogram;
import org.sef4j.callstack.stats.PerfStats;

public class PerfStatsPredicates {

	public static class MinPendingCountPredicate implements Predicate<PerfStats> {
		public static final MinPendingCountPredicate INSTANCE = new MinPendingCountPredicate(0);
		
		private final int minCount;
		public MinPendingCountPredicate(int minCount) {
			this.minCount = minCount;
		}

		@Override
		public boolean test(PerfStats src) {
			return src.getPendingCounts().getPendingCount() > minCount;
		}
	}


	public static class ElapsedTimeDelegatePerfStatsPredicate implements Predicate<PerfStats> {
		private final Predicate<BasicTimeStatsLogHistogram> delegate;
		public ElapsedTimeDelegatePerfStatsPredicate(Predicate<BasicTimeStatsLogHistogram> delegate) {
			this.delegate = delegate;
		}
		@Override
		public boolean test(PerfStats src) {
			return delegate.test(src.getElapsedTimeStats());
		}
	}

	public static class ThreadUserTimeDelegatePerfStatsPredicate implements Predicate<PerfStats> {
		private final Predicate<BasicTimeStatsLogHistogram> delegate;
		public ThreadUserTimeDelegatePerfStatsPredicate(Predicate<BasicTimeStatsLogHistogram> delegate) {
			this.delegate = delegate;
		}
		@Override
		public boolean test(PerfStats src) {
			return delegate.test(src.getThreadUserTimeStats());
		}
	}

	public static class ThreadCpuTimeDelegatePerfStatsPredicate implements Predicate<PerfStats> {
		private final Predicate<BasicTimeStatsLogHistogram> delegate;
		public ThreadCpuTimeDelegatePerfStatsPredicate(Predicate<BasicTimeStatsLogHistogram> delegate) {
			this.delegate = delegate;
		}
		@Override
		public boolean test(PerfStats src) {
			return delegate.test(src.getThreadCpuTimeStats());
		}
	}

}
