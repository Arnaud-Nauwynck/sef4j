package org.sef4j.callstack.stattree;

import org.junit.Assert;
import org.junit.Test;


public class CallTreeNodeDTOTest {

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
