package org.sef4j.callstack.stats;

import java.util.concurrent.Callable;


/**
 * performance time statistics and counters using small Log-based histogram
 * <BR/>
 * This class is multi-thread safe, and lock FREE !!
 * <BR/>
 * 
 * Ranges are hard-coded with 10 buckets, using this logarithmic range: 
 * <ul>
 * <li> [0]: 0           millis</li>
 * <li> [1]: 1    - 31   millis</li>
 * <li> [2]: 32   - 63   millis</li>
 * <li> [3]: 64   - 127  millis</li>
 * <li> [4]: 128  - 255  millis</li>
 * <li> [5]: 256  - 511  millis</li>
 * <li> [6]: 512  - 1023 millis</li>
 * <li> [7]: 1024 - 2047 millis</li>
 * <li> [8]: 2048 - 4095 millis</li>
 * <li> [9]: more than 4096 millis</li>
 * </ul> 
 */
@SuppressWarnings("restriction")
public final class BasicTimeStatsLogHistogram {

	/**
	 * slot count
	 */
	public static final int SLOT_LEN = 10;
	

	/**
	 * occurrence count per elapsed time using histogram slots
	 * 
	 * (values updated atomically using code similar to AtomicIntegerArray using UNSAFE.getAndAddInt() / .getIntVolatile()
	 * but optimized: without using wrapper class + extra array index bound checking
	 * )
	 */
	private int[] countSlots = new int[SLOT_LEN];

	/**
	 * sum of elapsed time using histogram slots
	 * 
	 * (values updated atomically using code similar to AtomicLongArray using UNSAFE.getAndAddLong() / .getLongVolatile() 
	 * but optimized: without using wrapper class + extra array index bound checking
	 * )
	 */
	private long[] sumSlots = new long[SLOT_LEN];
	
	
	// ------------------------------------------------------------------------

	public BasicTimeStatsLogHistogram() {
	}

	public static final Callable<BasicTimeStatsLogHistogram> FACTORY = new Callable<BasicTimeStatsLogHistogram>() {
        @Override
        public BasicTimeStatsLogHistogram call() throws Exception {
            return new BasicTimeStatsLogHistogram();
        }
    };
    
	// ------------------------------------------------------------------------

	public void incr(long value) {
		int index = valueToSlotIndex(value);
		UNSAFE.getAndAddInt(countSlots, byteOffsetInt(index), 1);
		UNSAFE.getAndAddLong(sumSlots, byteOffsetLong(index), value);
	}
	
	
	// ------------------------------------------------------------------------

	/** @return sum of values in all slots */
	public long getSlotsSum() {
		long res = 0;
		for (int i = 0; i < SLOT_LEN; i++) {
			res += getSum(i);
		}
		return res;
	}

	/** @return sum of counts in all slots */
	public int getSlotsCount() {
		int res = 0;
		for (int i = 0; i < SLOT_LEN; i++) {
			res += getCount(i);
		}
		return res;
	}

	/** @return copy of all slots */
	public BasicTimeStatsSlotInfo[] getSlotInfoCopy() {
		BasicTimeStatsSlotInfo[] res = new BasicTimeStatsSlotInfo[SLOT_LEN];
		for (int i = 0; i < SLOT_LEN; i++) {
			BasicTimeStatsSlotInfo slotInfo = SLOT_INFOS[i];
			res[i] = new BasicTimeStatsSlotInfo(slotInfo.getFrom(), slotInfo.getTo(), getCount(i), getSum(i));
		}
		return res;
	}

	/** @return copy of nth-slot */
	public BasicTimeStatsSlotInfo getSlotInfoCopyAt(int i) {
		if (i < 0 || i >= SLOT_LEN) throw new ArrayIndexOutOfBoundsException();
		BasicTimeStatsSlotInfo slotInfo = SLOT_INFOS[i];
		return new BasicTimeStatsSlotInfo(slotInfo.getFrom(), slotInfo.getTo(), getCount(i), getSum(i));
	}

	
	public void getCopyTo(BasicTimeStatsLogHistogram dest) {
		for (int i = 0; i < SLOT_LEN; i++) {
			dest.countSlots[i] = getCount(i);
			dest.sumSlots[i] = getSum(i);
		}
	}

	public BasicTimeStatsLogHistogram getCopy() {
		BasicTimeStatsLogHistogram res = new BasicTimeStatsLogHistogram();
		getCopyTo(res);
		return res;
	}

	public boolean compareHasChangeCount(BasicTimeStatsLogHistogram cmp) {
		for (int i = 0; i < SLOT_LEN; i++) {
			if (getCount(i) != cmp.getCount(i)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		long count = getSlotsCount();
		long avg = (count != 0)? getSlotsSum()/count : 0;
		return "PerfStatsHistogram ["
				+ "count:" + count 
				+ ", avg:" + avg
				+ "]";
	}
	
	// internal utilities for log-based index
	// ------------------------------------------------------------------------


	private static final BasicTimeStatsSlotInfo[] SLOT_INFOS;
	private static final int MAX_SLOT_VALUE = 4096;
	private static final int[] VALUE_DIV32_TO_SLOT_INDEX; 

	static {
		int[] breaks = new int[] { 
				1, 32, 64, 128, 256, 512, 1024, 2048, 4096 
		};

		BasicTimeStatsSlotInfo[] tmp = new BasicTimeStatsSlotInfo[SLOT_LEN];
		int[] tmpValueToSlotIndex = new int[MAX_SLOT_VALUE/32];
		
		tmp[0] = new BasicTimeStatsSlotInfo(-Long.MAX_VALUE, 0, 0, 0);
//		System.out.println("0: [-INF, 0]");
		int index = 1;
		int from = 1;
		for(int i = 1; i <= MAX_SLOT_VALUE; i++) {
			if (breaks[index] == i) {
				tmp[index] = new BasicTimeStatsSlotInfo(from, i-1, 0, 0);
//				System.out.println(index + ": [" + from + ", " + (i-1) + "]");
				index++;
				from = i;
			}
			if (i == MAX_SLOT_VALUE) break;
			int iDiv32 = i >>> 5;
			tmpValueToSlotIndex[iDiv32] = index;
		}
		tmp[SLOT_LEN-1] = new BasicTimeStatsSlotInfo(from, Long.MAX_VALUE, 0, 0);
//		System.out.println((SLOT_LEN-1) + ": [" + from + ", +INF]");
		
		SLOT_INFOS = tmp;
		VALUE_DIV32_TO_SLOT_INDEX = tmpValueToSlotIndex;
		
		// check..
		if (1 != valueToSlotIndex(30)) throw new IllegalStateException();
		if (1 != valueToSlotIndex(31)) throw new IllegalStateException();
		if (2 != valueToSlotIndex(32)) throw new IllegalStateException();
		if (2 != valueToSlotIndex(33)) throw new IllegalStateException();
		
		if (SLOT_LEN-2 != valueToSlotIndex(4095)) throw new IllegalStateException();
		if (SLOT_LEN-1 != valueToSlotIndex(4096)) throw new IllegalStateException();
		
		index = 1;
		for(int i = 1; i < MAX_SLOT_VALUE; i++) {
			if (breaks[index] == i) {
				index++;
			}
			int checkSlot = valueToSlotIndex(i);
			if (checkSlot != index) {
				throw new IllegalStateException("ERROR " + i + " => " + checkSlot + " != " + index);
			}
		}
		
	}

	
	/**
	 * index using logarithm / linear by parts 
	 */
	public static int valueToSlotIndex(long value) {
		if (value <= 0) return 0;
		else if (value >= MAX_SLOT_VALUE) return SLOT_LEN-1;
		int v = (int) value;
		if (v < 32) return 1;
		else return VALUE_DIV32_TO_SLOT_INDEX[v >>> 5];
	}

	// internal utilities of UNSAFE memory access for atomic operation, without locks 
	// ------------------------------------------------------------------------

	private static sun.misc.Unsafe UNSAFE;
	private static final long ARRAY_BASE_OFFSET; // = 16...
	private static final int shiftInt; // = 2 ...
	private static final int shiftLong;  // = 3 ...
	
	static {
		UNSAFE = UnsafeUtils.getUnsafe();
		
		ARRAY_BASE_OFFSET = UNSAFE.arrayBaseOffset(byte[].class); // = 16...
		
		int scaleInt = UNSAFE.arrayIndexScale(int[].class);  // =4... size of an "int" in an int[] array
        shiftInt = 31 - Integer.numberOfLeadingZeros(scaleInt);
        
        int scaleLong = UNSAFE.arrayIndexScale(long[].class); // =8... size of a "long" in a long[] array
        shiftLong = 31 - Integer.numberOfLeadingZeros(scaleLong);
    }

    private static long byteOffsetInt(int i) {
        return ((long) i << shiftInt) + ARRAY_BASE_OFFSET;
    }
    
    private static long byteOffsetLong(int i) {
        return ((long) i << shiftLong) + ARRAY_BASE_OFFSET;
    }

    
	private int getCount(int index) {
		assert index >= 0 && index < SLOT_LEN;
		return UNSAFE.getIntVolatile(countSlots, byteOffsetInt(index));
	}
	
	private long getSum(int index) {
		assert index >= 0 && index < SLOT_LEN;
		return UNSAFE.getLongVolatile(sumSlots, byteOffsetLong(index));
	}

}
