package org.sef4j.callstack.stats;


import org.junit.Assert;
import org.junit.Test;

public class PerfTimeStatsTest {

	@Test
	public void testAdd() {
		PerfTimeStats sut = new PerfTimeStats();

		int add = 0;
		sut.incr(add);
		int index = PerfTimeStats.valueToSlotIndex(add);
		PerfTimeStatsSlotInfo[] copy = sut.getSlotInfoCopy();
		Assert.assertEquals(1, copy[index].getCount());
		Assert.assertEquals(add, copy[index].getSum());
		
		add = 20;
		sut.incr(add);
		index = PerfTimeStats.valueToSlotIndex(add);
		copy = sut.getSlotInfoCopy();
		Assert.assertEquals(1, copy[index].getCount());
		Assert.assertEquals(add, copy[index].getSum());

		sut.incr(add);
		copy = sut.getSlotInfoCopy();
		Assert.assertEquals(2, copy[index].getCount());
		Assert.assertEquals(2*add, copy[index].getSum());
		
	}
	
	
	@Test
	public void test_1_to_1000() {
		PerfTimeStats sut = new PerfTimeStats();
		
		for(int i = 0; i < 10000; i++) {
			sut.incr(i);
		}
		
		PerfTimeStatsSlotInfo[] slotInfo = sut.getSlotInfoCopy();
		Assert.assertEquals(1, slotInfo[0].getCount());
		
		Assert.assertEquals(31, slotInfo[1].getCount()); // [1, 31]
		Assert.assertEquals(16.0, slotInfo[1].getAverage(), 1e-6);

		Assert.assertEquals(32, slotInfo[2].getCount()); // [32,63]
		Assert.assertEquals((32+63)*.5, slotInfo[2].getAverage(), 1e-6);
		
		Assert.assertEquals(64, slotInfo[3].getCount()); // 64, 127
		
		Assert.assertEquals(128, slotInfo[4].getCount());
		Assert.assertEquals(256, slotInfo[5].getCount()); // [256, 511]
		Assert.assertEquals(512, slotInfo[6].getCount()); // [512, 1024]
		
		for (int i = 0; i < slotInfo.length; i++) {
			PerfTimeStatsSlotInfo s = slotInfo[i];
			double avg = ((double) s.getSum()) / s.getCount();
			System.out.println("[" + s.getFrom() + " , " + s.getTo() + "] : " + s.getCount() + "x ~" + avg);
		}
	}
}
