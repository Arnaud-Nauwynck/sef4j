package org.sef4j.callstack.stats;

import java.util.ArrayList;
import java.util.List;

import org.sef4j.core.api.proptree.PropTreeNodeMapper;
import org.sef4j.core.api.proptree.PropTreeNodeMapper.PropMapperEntry;
import org.sef4j.core.api.proptree.PropTreeValuePredicate;

public class PerfStatsUtils {

	public static PropTreeNodeMapper createDefaultPerfStatsDTOMapper() {
		return createDefaultPerfStatsDTOMapper(true, true, true, true,
				0, 0, 0);
	}
	
	public static PropTreeNodeMapper createDefaultPerfStatsDTOMapper(
			boolean useStatsPendingCount, 
			boolean useStatsCumulElapsed, boolean useStatsCumulThreadCpu, boolean useStatsCumulThreadUser,
			int filterMinPendingCount,
			int filterMinCount, long filterMinSum
			) {
		List<PropMapperEntry> propMapperEntries = new ArrayList<PropMapperEntry>();

		PropTreeValuePredicate predStats = new BasicTimeStatsLogHistogram.MinCountPropTreeValuePredicate(filterMinCount, filterMinSum);
		
		if (useStatsPendingCount) {
			PropTreeValuePredicate predMinPendingCount = (filterMinPendingCount != -1)?
					new PerfStats.MinCountPropTreeValuePredicate(filterMinPendingCount) : null;
			propMapperEntries.add(new PropMapperEntry("stats", "stats.pendingCount",
					PerfStats.PendingPerfCountDTOMapper.INSTANCE,
					predMinPendingCount));
		}
		if (useStatsCumulElapsed) {
			propMapperEntries.add(new PropMapperEntry("stats", "stats.cumulElapsed",
					PerfStats.CumulatedElapsedBasicTimeStatsLogHistogramDTOMapper.INSTANCE,
					predStats));
		}
		if (useStatsCumulThreadUser) {
			propMapperEntries.add(new PropMapperEntry("stats", "stats.cumulThreadUser",
					PerfStats.CumulatedThreadUserBasicTimeStatsLogHistogramDTOMapper.INSTANCE,
					predStats));
		}

		if (useStatsCumulThreadCpu) {
			propMapperEntries.add(new PropMapperEntry("stats", "stats.cumulThreadCpu",
					PerfStats.CumulatedThreadCpuBasicTimeStatsLogHistogramDTOMapper.INSTANCE,
					predStats));
		}

		return new PropTreeNodeMapper(propMapperEntries);
	}
	
	
}
