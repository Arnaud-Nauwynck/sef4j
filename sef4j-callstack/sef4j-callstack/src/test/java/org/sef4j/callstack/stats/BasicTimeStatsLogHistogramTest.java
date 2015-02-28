package org.sef4j.callstack.stats;


import org.junit.Assert;
import org.junit.Test;

public class BasicTimeStatsLogHistogramTest {

	BasicTimeStatsLogHistogram sut = new BasicTimeStatsLogHistogram();

	@Test
	public void testPerfTimeStats() {
		BasicTimeStatsSlotInfo[] slotInfos = sut.getSlotInfoCopy();

		assertEquals(-9223372036854775807L, 0, 0, 0, slotInfos[0]);
		assertEquals(1, 31, 0, 0, slotInfos[1]);
		assertEquals(32, 63, 0, 0, slotInfos[2]);
		assertEquals(64, 127, 0, 0, slotInfos[3]);
		assertEquals(128, 255, 0, 0, slotInfos[4]);
		assertEquals(256, 511, 0, 0, slotInfos[5]);
		assertEquals(512, 1023, 0, 0, slotInfos[6]);
		assertEquals(1024, 2047, 0, 0, slotInfos[7]);
		assertEquals(2048, 4095, 0, 0, slotInfos[8]);
		assertEquals(4096, 9223372036854775807L, 0, 0, slotInfos[9]);
	}
	
	@Test
	public void testValueToSlotIndex() {
		// test slot[0]
		// Prepare
		BasicTimeStatsSlotInfo[] slotInfos = sut.getSlotInfoCopy();
		// Perform
		sut.incr(0);
		// Post-check
		BasicTimeStatsSlotInfo slotCopy0 = sut.getSlotInfoCopyAt(0);
		assertEquals(Long.MIN_VALUE+1, 0, 1, 0, slotCopy0);

		// foreach slotIndex * (Prepare,Perform,Post-check)
		for(int slotIndex = 1; slotIndex < slotInfos.length-1; slotIndex++) {
			long from = slotInfos[slotIndex].getFrom();
			long to = slotInfos[slotIndex].getTo();
			BasicTimeStatsSlotInfo prevSlotCopy = sut.getSlotInfoCopyAt(slotIndex);
			for (long i = from; i < to; i++) {
				// Perform
				sut.incr(i);
				// Post-check
				BasicTimeStatsSlotInfo slotCopy = sut.getSlotInfoCopyAt(slotIndex);
				assertEquals(from, to, prevSlotCopy.getCount() + 1, prevSlotCopy.getSum() + i, slotCopy);
				prevSlotCopy = slotCopy;
			}
		}

		// test slot[SLOT_LEN]
		// Prepare
		long bigValue = 10000;
		// Perform
		sut.incr(bigValue);
		// Post-check
		BasicTimeStatsSlotInfo slotCopyLast = sut.getSlotInfoCopyAt(slotInfos.length-1);
		assertEquals(4096, Long.MAX_VALUE, 1, bigValue, slotCopyLast);
	}
	
	@Test
	public void testIncr() {
		// Prepare
		int add = 0;
		// Perform
		sut.incr(add);
		// Post-check
		int index = BasicTimeStatsLogHistogram.valueToSlotIndex(add);
		BasicTimeStatsSlotInfo[] copy = sut.getSlotInfoCopy();
		Assert.assertEquals(1, copy[index].getCount());
		Assert.assertEquals(add, copy[index].getSum());

		// Prepare
		add = 20;
		// Perform
		sut.incr(add);
		// Post-check
		index = BasicTimeStatsLogHistogram.valueToSlotIndex(add);
		copy = sut.getSlotInfoCopy();
		Assert.assertEquals(1, copy[index].getCount());
		Assert.assertEquals(add, copy[index].getSum());
		
		// Prepare
		// Perform
		sut.incr(add);		
		// Post-check
		copy = sut.getSlotInfoCopy();
		Assert.assertEquals(2, copy[index].getCount());
		Assert.assertEquals(2*add, copy[index].getSum());
		
	}
	
	
	@Test
	public void test_1_to_1000() {
		BasicTimeStatsLogHistogram sut = new BasicTimeStatsLogHistogram();
		
		for(int i = 0; i < 10000; i++) {
			sut.incr(i);
		}
		
		BasicTimeStatsSlotInfo[] slotInfo = sut.getSlotInfoCopy();
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
			BasicTimeStatsSlotInfo s = slotInfo[i];
			double avg = ((double) s.getSum()) / s.getCount();
			System.out.println("[" + s.getFrom() + " , " + s.getTo() + "] : " + s.getCount() + "x ~" + avg);
		}
	}
	
	@Test
	public void testGetSlotInfoCopy() {
		// Prepare
		// Perform
		// Post-check
		BasicTimeStatsSlotInfo[] slotInfos = sut.getSlotInfoCopy();
		int len = slotInfos.length;
		for(int i = 0; i < len; i++) {
			BasicTimeStatsSlotInfo s = slotInfos[i];
			Assert.assertEquals(0, s.getCount());
			Assert.assertEquals(0, s.getSum());
			// System.out.println("[" + s.getFrom() + "-" + s.getTo() + "] count:" + s.getCount() + ", sum:" + s.getSum()					);
		}
		
		assertEquals(-9223372036854775807L, 0, 0, 0, slotInfos[0]);
		assertEquals(1 , 31, 0, 0, slotInfos[1]);
		assertEquals(32 , 63, 0, 0, slotInfos[2]);
		assertEquals(64 , 127, 0, 0, slotInfos[3]);
		assertEquals(128 , 255, 0, 0, slotInfos[4]);
		assertEquals(256 , 511, 0, 0, slotInfos[5]);
		assertEquals(512 , 1023, 0, 0, slotInfos[6]);
		assertEquals(1024 , 2047, 0, 0, slotInfos[7]);
		assertEquals(2048 , 4095, 0, 0, slotInfos[8]);
		assertEquals(4096 , 9223372036854775807L, 0, 0, slotInfos[9]);
	}

	private static void assertEquals(long expectedFrom, long expectedTo, int expectedCount, long expectedSum, BasicTimeStatsSlotInfo actual) {
		Assert.assertEquals(expectedFrom, actual.getFrom());
		Assert.assertEquals(expectedTo, actual.getTo());
		Assert.assertEquals(expectedCount, actual.getCount());
		Assert.assertEquals(expectedSum, actual.getSum());
	}
	
	@Test
	public void testGetSlotInfoCopyAt() {
		// Prepare
		// Perform
		sut.incr(1);
		sut.incr(1);
		// Post-check
		BasicTimeStatsSlotInfo s = sut.getSlotInfoCopyAt(1);
		Assert.assertEquals(2, s.getCount());
		Assert.assertEquals(2, s.getSum());
	}
	
}
