package org.sef4j.callstack.stats.helpers;

import java.util.function.Predicate;

import org.sef4j.callstack.stats.BasicTimeStatsLogHistogram;
import org.sef4j.callstack.stats.PerfStats;
import org.sef4j.callstack.stats.helpers.PerfStatsDTOMapper.CumulatedElapsedBasicTimeStatsLogHistogramDTOMapper;
import org.sef4j.callstack.stats.helpers.PerfStatsDTOMapper.CumulatedThreadCpuBasicTimeStatsLogHistogramDTOMapper;
import org.sef4j.callstack.stats.helpers.PerfStatsDTOMapper.CumulatedThreadUserBasicTimeStatsLogHistogramDTOMapper;
import org.sef4j.callstack.stats.helpers.PerfStatsDTOMapper.PendingPerfCountDTOMapper;
import org.sef4j.core.api.proptree.PropTreeNode;
import org.sef4j.core.api.proptree.PropTreeNodeDTOMapper;
import org.sef4j.core.api.proptree.PropTreeNodeDTOMapper.PropMapperEntry;

public class PerfStatsDTOMapperUtils {

	public static PropTreeNodeDTOMapper createDTOMapper() {
		return createDTOMapper(0, 0, 0, 0, 0);
	}

	/**
	 * mapper for PropTreeNode (props: PerfStats stats) 
	 *   -> PropTreeNodeDTO (props: PerfStatsDTO stats)
	 */
	public static PropTreeNodeDTOMapper createDTOMapper(
			int filterMinPendingCount, int filterMinCount, 
			long filterMinSumElapsed, long filterMinSumThreadUserTime, long filterMinSumThreadCpuTime
			) {
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

		PropMapperEntry propMap = new PropMapperEntry("stats", "stats",
				PerfStatsDTOMapper.INSTANCE,
				null, pred);

		return new PropTreeNodeDTOMapper.Builder()
			.withPropMapperEntries(propMap)
			.build();
	}

	
	/**
	 * mapper for PropTreeNode (props: PerfStats stats) 
	 *   -> PropTreeNodeDTO (props: PendingCount pendingCount)
	 */
	public static PropTreeNodeDTOMapper createPendingCountFilterDTOMapper(
			final int filterMinPendingCount
			) {
		Predicate<PerfStats> pred = (filterMinPendingCount != -1)?
				new PerfStatsPredicates.MinPendingCountPredicate(filterMinPendingCount) : null;

		Predicate<PropTreeNode> recurseNodePredicate = (filterMinPendingCount != -1)?
				new Predicate<PropTreeNode>() {
			public boolean test(PropTreeNode node) {
				PerfStats perfStats = (PerfStats) node.getPropOrNull("stats");
				if (perfStats != null && perfStats.getPendingCount() > filterMinPendingCount) {
					return true;
				}
				return false;
			}
		} : null;

		return new PropTreeNodeDTOMapper.Builder()
			.withPropMapperEntries(new PropMapperEntry("stats", "pending",
				PendingPerfCountDTOMapper.INSTANCE,
				null, pred))
			.withRecuseNodePredicate(recurseNodePredicate)
			.build();		
	}
	
				
	
	// deprecated ?
	// ------------------------------------------------------------------------
	
	public static PropTreeNodeDTOMapper createPropExtractorDTOMapper() {
		return createPropExtractorDTOMapper(true, true, true, true,
				0, 0, 0);
	}
	
	/**
	 * mapper for PropTreeNode (props: stats) 
	 *   -> extract single PerfStats to several DTO properties 
	 *      PropTreeNodeDTO (props: stats.pendingCount, stats.cumulElapsedTime, cumulThreadUserTime, cumulThreadCpuTime)
	 */
	public static PropTreeNodeDTOMapper createPropExtractorDTOMapper(
			boolean useStatsPendingCount, 
			boolean useStatsCumulElapsed, boolean useStatsCumulThreadCpu, boolean useStatsCumulThreadUser,
			int filterMinPendingCount,
			int filterMinCount, long filterMinSum
			) {
		PropTreeNodeDTOMapper.Builder builder = new PropTreeNodeDTOMapper.Builder();

		Predicate<BasicTimeStatsLogHistogram> timeStatsPred = 
				new BasicTimeStatsLogHistogram.MinCountPropTreeValuePredicate(filterMinCount, filterMinSum);
		
		if (useStatsPendingCount) {
			Predicate<PerfStats> pred = (filterMinPendingCount != -1)?
					new PerfStatsPredicates.MinPendingCountPredicate(filterMinPendingCount) : null;
			 		
			builder.withPropMapperEntries(new PropMapperEntry("stats", "stats.pendingCount",
					PendingPerfCountDTOMapper.INSTANCE,
					null, pred));
		}
		if (useStatsCumulElapsed) {
			builder.withPropMapperEntries(new PropMapperEntry("stats", "stats.cumulElapsedTime",
					CumulatedElapsedBasicTimeStatsLogHistogramDTOMapper.INSTANCE,
					null, timeStatsPred));
		}
		if (useStatsCumulThreadUser) {
			builder.withPropMapperEntries(new PropMapperEntry("stats", "stats.cumulThreadUserTime",
					CumulatedThreadUserBasicTimeStatsLogHistogramDTOMapper.INSTANCE,
					null, timeStatsPred));
		}

		if (useStatsCumulThreadCpu) {
			builder.withPropMapperEntries(new PropMapperEntry("stats", "stats.cumulThreadCpuTime",
					CumulatedThreadCpuBasicTimeStatsLogHistogramDTOMapper.INSTANCE,
					null, timeStatsPred));
		}

		return builder.build();
	}
	
}
