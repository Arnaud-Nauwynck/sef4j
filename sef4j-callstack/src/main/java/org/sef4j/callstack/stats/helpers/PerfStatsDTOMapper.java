package org.sef4j.callstack.stats.helpers;

import org.sef4j.callstack.stats.PerfStats;
import org.sef4j.callstack.stats.dto.CumulatedBasicTimeStatsLogHistogramDTO;
import org.sef4j.callstack.stats.dto.PendingPerfCountDTO;
import org.sef4j.callstack.stats.dto.PerfStatsDTO;
import org.sef4j.core.api.proptree.PropTreeValueMapper.AbstractTypedPropTreeValueMapper;

public class PerfStatsDTOMapper extends AbstractTypedPropTreeValueMapper<PerfStats,PerfStatsDTO> {

	public static final PerfStatsDTOMapper INSTANCE = new PerfStatsDTOMapper();
	
	public PerfStatsDTO mapProp(PerfStats src) {
		return new PerfStatsDTO(src);
	}
	
	/** deprecated? Mapper for extract PerfStats.pendingCount to DTO */
	public static final class PendingPerfCountDTOMapper extends AbstractTypedPropTreeValueMapper<PerfStats,PendingPerfCountDTO> {
		public static final PendingPerfCountDTOMapper INSTANCE = new PendingPerfCountDTOMapper();
		public PendingPerfCountDTO mapProp(PerfStats src) {
			return new PendingPerfCountDTO(src.getPendingCounts());
		}
	}

	/** Mapper for extract PerfStats.elapsedTime to DTO */
	public static final class CumulatedElapsedBasicTimeStatsLogHistogramDTOMapper 
	extends AbstractTypedPropTreeValueMapper<PerfStats,CumulatedBasicTimeStatsLogHistogramDTO> {
		public static final CumulatedElapsedBasicTimeStatsLogHistogramDTOMapper INSTANCE = new CumulatedElapsedBasicTimeStatsLogHistogramDTOMapper();
		public CumulatedBasicTimeStatsLogHistogramDTO mapProp(PerfStats src) {
			return new CumulatedBasicTimeStatsLogHistogramDTO(src.getElapsedTimeStats());
		}
	}
	
	/** Mapper for extract PerfStats.threadUserTime to DTO */
	public static final class CumulatedThreadUserBasicTimeStatsLogHistogramDTOMapper extends AbstractTypedPropTreeValueMapper<PerfStats,CumulatedBasicTimeStatsLogHistogramDTO> {
		public static final CumulatedThreadUserBasicTimeStatsLogHistogramDTOMapper INSTANCE = new CumulatedThreadUserBasicTimeStatsLogHistogramDTOMapper();
		public CumulatedBasicTimeStatsLogHistogramDTO mapProp(PerfStats src) {
			return new CumulatedBasicTimeStatsLogHistogramDTO(src.getThreadUserTimeStats());
		}
	}

	/** Mapper for extract PerfStats.threadCpuTime to DTO */
	public static final class CumulatedThreadCpuBasicTimeStatsLogHistogramDTOMapper extends AbstractTypedPropTreeValueMapper<PerfStats,CumulatedBasicTimeStatsLogHistogramDTO> {
		public static final CumulatedThreadCpuBasicTimeStatsLogHistogramDTOMapper INSTANCE = new CumulatedThreadCpuBasicTimeStatsLogHistogramDTOMapper();
		public CumulatedBasicTimeStatsLogHistogramDTO mapProp(PerfStats src) {
			return new CumulatedBasicTimeStatsLogHistogramDTO(src.getThreadCpuTimeStats());
		}
	}

}
