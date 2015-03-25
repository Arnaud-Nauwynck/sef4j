package org.sef4j.core.api.proptree;

import org.junit.Assert;
import org.junit.Test;
import org.sef4j.core.api.proptree.PropTreeNode;


public class PropTreeNodeDTOTest {

	private PropTreeNode sut = PropTreeNode.newRoot();
	
	@Test
	public void testGetOrCreateChild() {
		// Prepare
		// Perform
		PropTreeNode child1 = sut.getOrCreateChild("child1");
		// Post-check
		Assert.assertNotNull(child1);
		PropTreeNode child1Bis = sut.getOrCreateChild("child1");
		Assert.assertSame(child1, child1Bis);
	}
	
	@Test
	public void testGetOrCreateChildPath() {
		// Prepare
		String[] path = new String[] { "child1", "child2" };
		// Perform
		PropTreeNode child_1_2 = sut.getOrCreateChildPath(path);
		// Post-check
		Assert.assertEquals("child2", child_1_2.getName());
		PropTreeNode child1 = child_1_2.getParent();
		Assert.assertEquals("child1", child1.getName());
		Assert.assertSame(sut, child1.getParent());
	}

}
