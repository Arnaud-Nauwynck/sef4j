package org.sef4j.core.helpers.adapters;

import org.junit.Assert;
import org.junit.Test;


public class TypeHierarchyToObjectMapTest {

    private static interface IA {}
    private static interface IA2 {}
    private static interface IB {}
    private static interface IB2 {}
    private static interface IC {}
    private static interface ICB extends IB {}
    private static interface IC2 {}
    private static interface ID {}
    private static interface ID2 {}
    private static interface IDCBD extends ICB {}

    private static class A implements IA, IA2 {}
    private static class B extends A implements IB, IB2 {}
    private static class C extends B implements IC, IC2, ICB {}
    private static class D extends C implements ID, ID2, IDCBD {}

    @Test
    public void testPutOverride() {
        // Prepare
        TypeHierarchyToObjectMap<Integer> sut = new TypeHierarchyToObjectMap<Integer>();
        // Perform
        sut.putOverride(B.class, 1);
        // Post-check
        Assert.assertEquals(1, (int) sut.getOverride(B.class));
    }
    
    @Test
    public void testGet_class() {
        // Prepare
        Integer i1 = 1;
        TypeHierarchyToObjectMap<Integer> sut = new TypeHierarchyToObjectMap<Integer>();
        sut.putOverride(B.class, i1);
        // Perform
        Assert.assertNull(sut.get(A.class));
        Assert.assertEquals(i1, sut.get(B.class));
        Assert.assertEquals(i1, sut.get(C.class));
        // Post-check
        
        // Prepare
        Integer i2 = 2;
        sut.putOverride(C.class, i2);
        // Perform
        Assert.assertNull(sut.get(A.class));
        Assert.assertEquals(i1, sut.get(B.class));
        Assert.assertEquals(i2, sut.get(C.class));
        Assert.assertEquals(i2, sut.get(D.class));
        // Post-check
    }
    
    @Test
    public void testGet_interface() {
        // Prepare
        Integer i1 = 1;
        TypeHierarchyToObjectMap<Integer> sut = new TypeHierarchyToObjectMap<Integer>();
        sut.putOverride(IB.class, i1);
        // Perform
        Assert.assertNull(sut.get(A.class));
        
        Assert.assertEquals(i1, sut.get(IB.class));
        Assert.assertEquals(i1, sut.get(B.class));
        Assert.assertEquals(i1, sut.get(ICB.class)); // should also find for sub interfaces ...
        
        Assert.assertNull(sut.get(IC.class));
        Assert.assertEquals(i1, sut.get(IDCBD.class)); // should also find in sub-sub interface 
        Assert.assertEquals(i1, sut.get(D.class));

        Assert.assertNull(sut.get(A.class));
        
        // Post-check
        
        // Prepare
        Integer i2 = 2;
        sut.putOverride(ICB.class, i2);
        // Perform
        Assert.assertNull(sut.get(A.class));
        Assert.assertEquals(i1, sut.get(B.class));
        Assert.assertEquals(i2, sut.get(C.class)); // ICB:2  (override IB:1) 
        Assert.assertEquals(i2, sut.get(D.class));
        // Post-check
    }
}
