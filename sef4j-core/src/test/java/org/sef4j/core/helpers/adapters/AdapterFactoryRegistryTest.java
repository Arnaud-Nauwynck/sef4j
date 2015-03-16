package org.sef4j.core.helpers.adapters;

import org.junit.Assert;
import org.junit.Test;


public class AdapterFactoryRegistryTest {

    private static interface IA {}
    private static interface IA2 {}
    private static interface IB {}
    private static interface IB2 {}
    private static interface IC {}
    private static interface ICB extends IB {}
    private static interface IC2 {}

    private static class A implements IA, IA2 {}
    private static class B extends A implements IB, IB2 {}
    private static class C extends B implements IC, IC2, ICB {}

    private static interface IFoo {} 
    private static class AIFooAdapter implements IFoo {
        private A delegate;
        public AIFooAdapter(A delegate) {
            this.delegate = delegate;
        }
    } 
    private static class AIFooAdapterFactory implements AdapterFactory<IFoo,A> {
        @Override
        public IFoo getAdapter(A obj) {
            return new AIFooAdapter(obj);
        }
    }
    
    private static class BIFooAdapter implements IFoo {
        private B delegate;
        public BIFooAdapter(B delegate) {
            this.delegate = delegate;
        }
    } 
    private static class BIFooAdapterFactory implements AdapterFactory<IFoo,B> {
        @Override
        public IFoo getAdapter(B obj) {
            return new BIFooAdapter(obj);
        }
    }
    

    @Test
    public void testGetAdapter_class() {
        // Prepare
        AdapterFactoryRegistry sut = new AdapterFactoryRegistry();
        A a = new A();
        B b = new B();
        C c = new C();
        
        sut.registerAdapterFactory(IFoo.class, B.class, new BIFooAdapterFactory());
        // Perform
        IFoo fooAAdapter = sut.getAdapter(a, IFoo.class);
        IFoo fooBAdapter = sut.getAdapter(b, IFoo.class);
        IFoo fooCAdapter = sut.getAdapter(c, IFoo.class);
        // Post-check
        Assert.assertNull(fooAAdapter);
        Assert.assertNotNull(fooBAdapter);
        Assert.assertTrue(fooBAdapter instanceof BIFooAdapter);
        Assert.assertNotNull(fooCAdapter);
        Assert.assertTrue(fooCAdapter instanceof BIFooAdapter);
        
        // Prepare
        sut.registerAdapterFactory(IFoo.class, A.class, new AIFooAdapterFactory());
        // Perform
        fooAAdapter = sut.getAdapter(a, IFoo.class);
        fooBAdapter = sut.getAdapter(b, IFoo.class);
        fooCAdapter = sut.getAdapter(c, IFoo.class);

        Assert.assertNotNull(fooAAdapter);
        Assert.assertTrue(fooAAdapter instanceof AIFooAdapter);
        Assert.assertSame(a, ((AIFooAdapter) fooAAdapter).delegate);
        Assert.assertNotNull(fooBAdapter);
        Assert.assertTrue(fooBAdapter instanceof BIFooAdapter);
        Assert.assertSame(b, ((BIFooAdapter) fooBAdapter).delegate);
        Assert.assertNotNull(fooCAdapter);
        Assert.assertTrue(fooCAdapter instanceof BIFooAdapter);
        Assert.assertSame(c, ((BIFooAdapter) fooCAdapter).delegate);
        // Post-check
    }
    
    
}
