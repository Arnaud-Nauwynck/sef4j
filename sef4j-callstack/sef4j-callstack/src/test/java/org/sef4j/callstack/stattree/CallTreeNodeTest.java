package org.sef4j.callstack.stattree;

import org.junit.Assert;
import org.junit.Test;


public class CallTreeNodeTest {

	private CallTreeNode sut = CallTreeNode.newRoot();
	
	@Test
	public void testGetOrCreateChild() {
		// Prepare
		// Perform
		CallTreeNode child1 = sut.getOrCreateChild("child1");
		// Post-check
		Assert.assertNotNull(child1);
		CallTreeNode child1Bis = sut.getOrCreateChild("child1");
		Assert.assertSame(child1, child1Bis);
	}
	
	@Test
	public void testGetPath() {
		// Prepare
		CallTreeNode child1 = sut.getOrCreateChild("child1");
		CallTreeNode child1_2 = child1.getOrCreateChild("child2");
		CallTreeNode child1_2_3 = child1_2.getOrCreateChild("child3");
		// Perform
		String[] path_1_2_3 = child1_2_3.getPath();
		// Post-check
		Assert.assertEquals(3,  path_1_2_3.length);
		Assert.assertEquals("child1",  path_1_2_3[0]);
		Assert.assertEquals("child2",  path_1_2_3[1]);
		Assert.assertEquals("child3",  path_1_2_3[2]);
	}

	@Test
	public void testGetReversePath() {
		// Prepare
		CallTreeNode child1 = sut.getOrCreateChild("child1");
		CallTreeNode child1_2 = child1.getOrCreateChild("child2");
		CallTreeNode child1_2_3 = child1_2.getOrCreateChild("child3");
		// Perform
		String[] reversePath_1_2_3 = child1_2_3.getReversePath();
		// Post-check
		Assert.assertEquals(3,  reversePath_1_2_3.length);
		Assert.assertEquals("child3",  reversePath_1_2_3[0]);
		Assert.assertEquals("child2",  reversePath_1_2_3[1]);
		Assert.assertEquals("child1",  reversePath_1_2_3[2]);
	}

	@Test
	public void testGetOrCreateChildPath() {
		// Prepare
		String[] path = new String[] { "child1", "child2" };
		// Perform
		CallTreeNode child_1_2 = sut.getOrCreateChildPath(path);
		// Post-check
		Assert.assertEquals("child2", child_1_2.getName());
		CallTreeNode child1 = child_1_2.getParent();
		Assert.assertEquals("child1", child1.getName());
		Assert.assertSame(sut, child1.getParent());
	}
}
