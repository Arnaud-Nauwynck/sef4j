package org.sef4j.callstack.export.influxdb.series;

import org.influxdb.dto.Serie;
import org.junit.Assert;
import org.junit.Test;
import org.sef4j.callstack.stats.PerfStats;


public class PerfStatsToSerieMapperTest {

	PerfStatsToSerieMapper sut = new PerfStatsToSerieMapper("", "", true, true, true, true);
	
	@Test
	public void testMap() {
		// Prepare
		PerfStats src = new PerfStats();
		// Perform
		Serie res = sut.map(src, "serie1");
		// Post-check
		Assert.assertNotNull(res);
	}
}
