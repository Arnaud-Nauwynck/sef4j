package org.sef4j.callstack.stats;

import org.junit.Assert;
import org.junit.Test;

import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public class UnsafeUtilsTest {

	@Test
    public void testGetUnsafe() {
		Unsafe res = UnsafeUtils.getUnsafe();
		Assert.assertNotNull(res);
	}
}
