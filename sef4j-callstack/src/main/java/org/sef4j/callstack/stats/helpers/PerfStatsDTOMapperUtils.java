package org.sef4j.callstack.stats.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.sef4j.callstack.stats.BasicTimeStatsLogHistogram;
import org.sef4j.callstack.stats.PerfStats;
import org.sef4j.callstack.stats.helpers.PerfStatsDTOMapper.CumulatedElapsedBasicTimeStatsLogHistogramDTOMapper;
import org.sef4j.callstack.stats.helpers.PerfStatsDTOMapper.CumulatedThreadCpuBasicTimeStatsLogHistogramDTOMapper;
import org.sef4j.callstack.stats.helpers.PerfStatsDTOMapper.CumulatedThreadUserBasicTimeStatsLogHistogramDTOMapper;
import org.sef4j.callstack.stats.helpers.PerfStatsDTOMapper.PendingPerfCountDTOMapper;
import org.sef4j.core.api.proptree.PropTreeNodeMapper;
import org.sef4j.core.api.proptree.PropTreeNodeMapper.PropMapperEntry;

public class PerfStatsDTOMapperUtils {

	public static PropTreeNodeMapper createDTOMapper() {
		return createDTOMapper(0, 0, 0, 0, 0);
	}

	/**
	 * mapper for PropTreeNode (props: PerfStats stats) 
	 *   -> PropTreeNodeDTO (props: PerfStatsDTO stats)
	 */
	public static PropTreeNodeMapper createDTOMapper(
			int filterMinPendingCount, int filterMinCount, 
			long filterMinSumElapsed, long filterMinSumThreadUserTime, long filterMinSumThreadCpuTime
			) {
		List<PropMapperEntry> propMapperEntries = new ArrayList<PropMapperEntry>();

		final Predicate<PerfStats> pendingCountPred = (filterMinPendingCount != -1)?
				new PerfStatsPredicates.MinPendingCountPredicate(filterMinPendingCount) : null;

		final Predicate<BasicTimeStatsLogHistogram> elapsedPred = 
				new BasicTimeStatsLogHistogram.MinCountPropTreeValuePredicate(filterMinCount, filterMinSumElapsed);
		final Predicate<BasicTimeStatsLogHistogram> threadUserTimePred = 
				new BasicTimeStatsLogHistogram.MinCountPropTreeValuePredicate(filterMinCount, filterMinSumThreadUserTime);
		final Predicate<BasicTimeStatsLogHistogram> threadCpuTimePred = 
				new BasicTimeStatsLogHistogram.MinCountPropTreeValuePredicate(filterMinCount, filterMinSumThreadCpuTime);
		
		Predicate<PerfStats> pred = new Predicate<PerfStats>() {
			public boolean test(PerfStats x) {
				return pendingCountPred.test(x) 
						|| elapsedPred.test(x.getElapsedTimeStats())
						|| threadUserTimePred.test(x.getThreadUserTimeStats())
						|| threadCpuTimePred.test(x.getThreadCpuTimeStats());
			}
		};

		propMapperEntries.add(new PropMapperEntry("stats", "stats",
				PerfStatsDTOMapper.INSTANCE,
				null, pred));

		return new PropTreeNodeMapper(propMapperEntries);
	}

	
	
	public static PropTreeNodeMapper createPropExtractorDTOMapper() {
		return createPropExtractorDTOMapper(true, true, true, true,
				0, 0, 0);
	}
	
	/**
	 * mapper for PropTreeNode (props: stats) 
	 *   -> PropTreeNodeDTO (props: stats.pendingCount, stats.cumulElapsedTime, cumulThreadUserTime, cumulThreadCpuTime)
	 */
	public static PropTreeNodeMapper createPropExtractorDTOMapper(
			boolean useStatsPendingCount, 
			boolean useStatsCumulElapsed, boolean useStatsCumulThreadCpu, boolean useStatsCumulThreadUser,
			int filterMinPendingCount,
			int filterMinCount, long filterMinSum
			) {
		List<PropMapperEntry> propMapperEntries = new ArrayList<PropMapperEntry>();

		Predicate<BasicTimeStatsLogHistogram> timeStatsPred = 
				new BasicTimeStatsLogHistogram.MinCountPropTreeValuePredicate(filterMinCount, filterMinSum);
		
		if (useStatsPendingCount) {
			Predicate<PerfStats> pred = (filterMinPendingCount != -1)?
					new PerfStatsPredicates.MinPendingCountPredicate(filterMinPendingCount) : null;
			 		
			propMapperEntries.add(new PropMapperEntry("stats", "stats.pendingCount",
					PendingPerfCountDTOMapper.INSTANCE,
					null, pred));
		}
		if (useStatsCumulElapsed) {
			propMapperEntries.add(new PropMapperEntry("stats", "stats.cumulElapsedTime",
					CumulatedElapsedBasicTimeStatsLogHistogramDTOMapper.INSTANCE,
					null, timeStatsPred));
		}
		if (useStatsCumulThreadUser) {
			propMapperEntries.add(new PropMapperEntry("stats", "stats.cumulThreadUserTime",
					CumulatedThreadUserBasicTimeStatsLogHistogramDTOMapper.INSTANCE,
					null, timeStatsPred));
		}

		if (useStatsCumulThreadCpu) {
			propMapperEntries.add(new PropMapperEntry("stats", "stats.cumulThreadCpuTime",
					CumulatedThreadCpuBasicTimeStatsLogHistogramDTOMapper.INSTANCE,
					null, timeStatsPred));
		}

		return new PropTreeNodeMapper(propMapperEntries);
	}
	
}
