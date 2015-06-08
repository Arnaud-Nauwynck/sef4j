package org.sef4j.core.helpers.proptree.model;

import org.junit.Assert;
import org.junit.Test;


public class PathTest {

	// private Path pathROOT = new Path(null, null);

	private Path pathAB = new Path(new Path(null, "a"), "b");
	private Path pathAA = new Path(new Path(null, "a"), "a");
	private Path pathBB = new Path(new Path(null, "b"), "b");
	private Path pathAC = new Path(new Path(null, "a"), "c");
	private Path pathBC = new Path(new Path(null, "b"), "c");
	private Path pathABC = new Path(new Path(new Path(null, "a"), "b"), "c");
	private Path pathADC = new Path(new Path(new Path(null, "a"), "d"), "c");

	@Test
	public void testOf() {
		Path res = Path.of("a");
		Assert.assertEquals("a", res.getLast());
		Assert.assertNull(res.getParentPath());

		res = Path.of("a", "b");
		Assert.assertEquals("b", res.getLast());
		Assert.assertEquals("a", res.getParentPath().getLast());
		Assert.assertNull(res.getParentPath().getParentPath());

		res = Path.of(Path.of("a"), "b");
		Assert.assertEquals("b", res.getLast());
		Assert.assertEquals("a", res.getParentPath().getLast());
		Assert.assertNull(res.getParentPath().getParentPath());
		
		Assert.assertEquals(pathABC, Path.of("a", "b", "c"));
	}

	@Test
	public void testToString() {
		Assert.assertEquals("a/b/c", pathABC.toString());
	}

	@Test
	public void testGetParent() {
		Assert.assertEquals(pathAB, pathABC.getParentPath());
	}

	@Test
	public void testToArray() {
		String[] res = pathAB.toArray();
		Assert.assertEquals(2, res.length);
		Assert.assertEquals("a", res[0]);
		Assert.assertEquals("b", res[1]);
	}

	@Test
	public void testToArray_index() {
		String[] res = pathABC.toArray(0, 3);
		Assert.assertEquals(3, res.length);
		Assert.assertEquals("a", res[0]);
		Assert.assertEquals("b", res[1]);
		Assert.assertEquals("c", res[2]);
		
		res = pathABC.toArray(0, 2);
		Assert.assertEquals(2, res.length);
		Assert.assertEquals("a", res[0]);
		Assert.assertEquals("b", res[1]);

		res = pathABC.toArray(1, 2);
		Assert.assertEquals(1, res.length);
		Assert.assertEquals("b", res[0]);

		res = pathABC.toArray(1, 3);
		Assert.assertEquals(2, res.length);
		Assert.assertEquals("b", res[0]);
		Assert.assertEquals("c", res[1]);
	}

	@Test
	public void testGetLength() {
		Assert.assertEquals(3, pathABC.getLength());
	}
	
	@Test
	public void testElementAt() {
		Assert.assertEquals("a", pathABC.elementAt(0));
		Assert.assertEquals("b", pathABC.elementAt(1));
		Assert.assertEquals("c", pathABC.elementAt(2));
		try {
			pathABC.elementAt(-1);
			Assert.fail();
		} catch(ArrayIndexOutOfBoundsException ex) {
			// ok
		}
		try {
			pathABC.elementAt(3);
			Assert.fail();
		} catch(ArrayIndexOutOfBoundsException ex) {
			// ok
		}
	}

	@Test
	public void testEquals() {
		Assert.assertTrue(pathABC.equals(Path.of("a", "b", "c")));
		Assert.assertTrue(pathABC.equals(pathABC));

		Assert.assertFalse(pathABC.equals(null));
		Assert.assertFalse(pathABC.equals(new Object()));
		Assert.assertFalse(pathAB.equals(pathAC));
		Assert.assertFalse(pathAB.equals(pathABC));
		Assert.assertFalse(pathABC.equals(pathAB));
	}

	@Test
	public void testCompareTo() {
		Assert.assertEquals(-1, pathAB.compareTo(pathABC));
		Assert.assertEquals(-1, pathAB.compareTo(pathAC));
		Assert.assertEquals(-1, pathABC.compareTo(pathAC));
		Assert.assertEquals(0, pathABC.compareTo(pathABC));
		Assert.assertEquals(+1, pathABC.compareTo(pathAB));
		Assert.assertEquals(+1, pathAC.compareTo(pathAB));
		Assert.assertEquals(+1, pathAC.compareTo(pathABC));
		Assert.assertTrue(pathABC.compareTo(pathADC) < 0); // -2 !!
	}

	@Test
	public void testRecursiveCompare() {
		Assert.assertEquals(+1, Path.recursiveCompare(pathAC, pathAB));
		Assert.assertEquals(-2, Path.recursiveCompare(pathABC, pathADC)); // -2 instead of -1 ??
	}
	
	@Test
	public void testStartsWith() {
		Assert.assertTrue(pathAB.startsWith(pathAB));
		Assert.assertTrue(pathABC.startsWith(pathAB));

		Assert.assertFalse(pathAB.startsWith(pathAC));
		Assert.assertFalse(pathAB.startsWith(pathABC));
	}

	@Test
	public void testEndsWith() {
		Assert.assertTrue(pathAB.endsWith(pathAB));
		Assert.assertTrue(pathABC.endsWith(pathBC));

		Assert.assertFalse(pathAB.endsWith(pathAC));
		Assert.assertFalse(pathBB.endsWith(pathAB));
		Assert.assertFalse(pathAB.endsWith(pathABC));
	}

	@Test
	public void testHashcode() {
		Assert.assertEquals(Path.of("a", "b").hashCode(), pathAB.hashCode());
		Assert.assertNotEquals(Path.of("a", "b").hashCode(), pathABC.hashCode());
	}

}
