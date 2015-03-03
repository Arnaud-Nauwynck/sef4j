package org.sef4j.callstack.stats;

import org.junit.Assert;
import org.junit.Test;


public class BasicTimeStatsSlotInfoTest {

	@Test
	public void test() {
		long from = 0, to = 1, sum = 1;
		int count = 1;
		BasicTimeStatsSlotInfo info = new BasicTimeStatsSlotInfo(from, to, count, sum);
		Assert.assertEquals(from, info.getFrom());
		Assert.assertEquals(to, info.getTo());
		}
	
}
