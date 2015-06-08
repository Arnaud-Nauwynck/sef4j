package org.sef4j.core.helpers.proptree.model;

import org.junit.Assert;
import org.junit.Test;


public class PropTreeValuePathTest {

	PropTreeValuePath root_prop1 = new PropTreeValuePath(null, "prop1");
	PropTreeValuePath ab_prop1 = new PropTreeValuePath(Path.of("a", "b"), "prop1");
	PropTreeValuePath abc_prop1 = new PropTreeValuePath(Path.of("a", "b", "c"), "prop1");
	PropTreeValuePath ab_prop2 = new PropTreeValuePath(Path.of("a", "b"), "prop2");

	@Test
	public void testPropTreeValuePath_null() {
		new PropTreeValuePath(null, "prop1");
	}

	@Test
	public void testGetPath() {
		Assert.assertEquals(Path.of("a", "b"), ab_prop1.getPath());
	}

	@Test
	public void testGetPropName() {
		Assert.assertEquals("prop1", ab_prop1.getPropName());
	}

	@Test
	public void testEquals() {
		Assert.assertTrue(ab_prop1.equals(ab_prop1));
		Assert.assertTrue(ab_prop1.equals(new PropTreeValuePath(Path.of("a", "b"), "prop1")));
		Assert.assertTrue(root_prop1.equals(root_prop1));

		Assert.assertFalse(root_prop1.equals(ab_prop1));
		Assert.assertFalse(ab_prop1.equals(root_prop1));
		Assert.assertFalse(ab_prop1.equals(ab_prop2));
		Assert.assertFalse(ab_prop1.equals(abc_prop1));
		Assert.assertFalse(ab_prop1.equals(null));
		Assert.assertFalse(ab_prop1.equals(new Object()));
	}

	@Test
	public void testHashCode() {
		Assert.assertEquals(ab_prop1.hashCode(), ab_prop1.hashCode());
		Assert.assertNotEquals(ab_prop2.hashCode(), ab_prop1.hashCode());
		Assert.assertEquals(root_prop1.hashCode(), root_prop1.hashCode());
	}
	
	@Test
	public void testCompareTo() {
		Assert.assertEquals(0, ab_prop1.compareTo(ab_prop1));
		Assert.assertEquals(-1, ab_prop1.compareTo(abc_prop1));
		Assert.assertEquals(-1, ab_prop1.compareTo(ab_prop2));
		Assert.assertEquals(+1, abc_prop1.compareTo(ab_prop1));
		Assert.assertEquals(-1, root_prop1.compareTo(ab_prop1));
		Assert.assertEquals(0, root_prop1.compareTo(root_prop1));
		Assert.assertEquals(+1, ab_prop1.compareTo(root_prop1));
	}

	@Test
	public void testToString() {
		Assert.assertEquals("a/b/-/prop1", ab_prop1.toString());
		Assert.assertEquals("/-/prop1", root_prop1.toString());
	}

}
