package com.google.code.joto.reflect;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;

public class SerializableMethodRef implements Serializable {

	/** */
	private static final long serialVersionUID = 1L;
	
	private final Class<?> methodClass;
	private final String methodName;
	private final Class<?>[] methodSignature;
	private transient Method method;
	
	//-------------------------------------------------------------------------


	public SerializableMethodRef(
			Class<?> methodClass, String methodName, Class<?>[] methodSignature) {
		super();
		this.methodClass = methodClass;
		this.methodName = methodName;
		this.methodSignature = methodSignature;
	}
	
	public SerializableMethodRef(Method m) {
		this(m.getDeclaringClass(), m.getName(), m.getParameterTypes());
		this.method = m;
	}

	//-------------------------------------------------------------------------

	public Class<?> getMethodClass() {
		return methodClass;
	}

	public String getMethodName() {
		return methodName;
	}

	public Class<?>[] getMethodSignature() {
		return methodSignature;
	}

	public Method getMethod() {
		if (method == null) {
			method = ReflectUtils.findMethod(methodClass, methodName, methodSignature);
		}
		return method;
	}

	// override java.lang.Object
	// -------------------------------------------------------------------------

	@Override
	public String toString() {
		return "SerializableMethodRef[" + methodClass.getName() + "#" + methodName + "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((methodClass == null) ? 0 : methodClass.hashCode());
		result = prime * result
				+ ((methodName == null) ? 0 : methodName.hashCode());
		result = prime * result + Arrays.hashCode(methodSignature);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SerializableMethodRef other = (SerializableMethodRef) obj;
		if (methodClass == null) {
			if (other.methodClass != null)
				return false;
		} else if (!methodClass.equals(other.methodClass))
			return false;
		if (methodName == null) {
			if (other.methodName != null)
				return false;
		} else if (!methodName.equals(other.methodName))
			return false;
		if (!Arrays.equals(methodSignature, other.methodSignature))
			return false;
		return true;
	}
	
	
}
