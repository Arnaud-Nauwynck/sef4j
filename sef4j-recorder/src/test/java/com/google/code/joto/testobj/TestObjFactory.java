package com.google.code.joto.testobj;

import java.io.Serializable;

public class TestObjFactory {

	public static SimpleIntFieldA createSimpleIntFieldA() {
		SimpleIntFieldA a = new SimpleIntFieldA();
		a.setFieldInt1(1);
		return a;
	}
	
	public static SimpleRefObjectFieldA createSimpleRefObjectFieldA() {
		SimpleRefObjectFieldA a = new SimpleRefObjectFieldA();
		a.setFieldObj(new SerializableObj());
		return a;
	}
	
	public static A createBeanA() {
		A a = new A();
		a.setFieldBoolean(true);
		a.setFieldBoolean2(false);
		a.setFieldInt(123);
		
		return a;
	}
	
	public static A createBeanA2() {
		A a = createBeanA();
	
		B b1 = new B();
		b1.setFieldInt(1);
		a.setFieldB(b1);

		a.setFieldB2(b1);

		C c = new C();
		c.setFieldInt(1);
		a.setFieldBC(c);

		B b2 = new B();
		b2.setFieldInt(1);
		a.getFieldBList().add(b2);
		a.getFieldBList().add(b2);

		B b3 = new B();
		a.addFieldBSet(b3);
		a.addFieldBSet(b2);

		B b4 = new B();
		a.putFieldBMap("key1", b4);
		a.putFieldBMap("key2", b2);

		return a;
	}

	public static NonSerializableA createNonSerializableA() {
		NonSerializableA a = new NonSerializableA();
		a.setFieldInt(123);
		
		return a;
	}

	public static NonSerializableA createNonSerializableA2() {
		NonSerializableA a = new NonSerializableA();
		a.setFieldInt(123);
		
		NonSerializableB b = new NonSerializableB();
		a.setB(b);
		
		return a;
	}

	public static SimpleRefA createSimpleRefA() {
		SimpleRefA a = new SimpleRefA();
		B b = new B();
		b.setFieldInt(2);
		a.setFieldB(b);
		return a;
	}
	
	public static SimpleRefBean createSimpleRefBean_Cyclic() {
		SimpleRefBean a = new SimpleRefBean();
		a.setFieldId(1);
		
		SimpleRefBean b = new SimpleRefBean();
		b.setFieldId(2);
		
		a.setRef(b);
		b.setRef(a);
		
		return a;
	}
	
	
	public static Serializable createAnySerializableBean(int i) {
		int mod = i % 6;
		switch(mod) {
		case 0: return createSimpleIntFieldA();
		case 1: return createSimpleRefObjectFieldA();
		case 2: return createBeanA();
		case 3: return createBeanA2();
		case 4: return createSimpleRefA();
		case 5: createSimpleRefBean_Cyclic();
		
		default:
			return createSimpleIntFieldA();
		}
	}

}