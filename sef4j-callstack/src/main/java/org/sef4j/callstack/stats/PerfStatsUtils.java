package org.sef4j.callstack.stats;

import java.util.HashMap;
import java.util.Map;

import org.sef4j.callstack.stats.CumulatedBasicTimeStatsLogHistogramDTO.CumulatedBasicTimeStatsLogHistogramDTOMapper;
import org.sef4j.core.api.proptree.PropTreeNodeMapper;
import org.sef4j.core.api.proptree.PropTreeValueMapper;
import org.sef4j.core.api.proptree.PropTreeValuePredicate;

public class PerfStatsUtils {
	
	public static PropTreeNodeMapper defaultPerfStatsDTOMapper() {
		Map<String,PropTreeValueMapper> propMappers = new HashMap<String,PropTreeValueMapper>();
		Map<String,PropTreeValuePredicate> propPredicates = new HashMap<String,PropTreeValuePredicate>();
		
		propMappers.put("stats", CumulatedBasicTimeStatsLogHistogramDTOMapper.INSTANCE);
		
		// propPredicates
		
		return new PropTreeNodeMapper(propMappers, propPredicates);
	}
}
