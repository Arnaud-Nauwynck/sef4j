package com.google.code.joto.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ClassJotoInfo {

	private final Class<?> targetClass;

	private List<ConstructorJotoInfo> constructorInfos = new ArrayList<ConstructorJotoInfo>();
	private List<MethodJotoInfo> methodInfos = new ArrayList<MethodJotoInfo>();
	
	//-------------------------------------------------------------------------

	public ClassJotoInfo(Class<?> targetClass) {
		this.targetClass = targetClass;
		
		for (Constructor<?> targetCtor : targetClass.getDeclaredConstructors()) {
			ConstructorJotoInfo ctorInfo = new ConstructorJotoInfo(this, targetCtor);
			constructorInfos.add(ctorInfo);
		}
		for (Method targetMethod : targetClass.getDeclaredMethods()) {
			MethodJotoInfo methodInfo = new MethodJotoInfo(this, targetMethod);
			methodInfos.add(methodInfo);
		}
	}

	//-------------------------------------------------------------------------

	public Class<?> getTargetClass() {
		return targetClass;
	}
	
	public List<MethodJotoInfo> getMethodInfos() {
		return methodInfos;
	}

	public List<ConstructorJotoInfo> getConstructorInfos() {
		return constructorInfos;
	}
	
	
	public ConstructorJotoInfo choosePublicCtorWithInfo() {
		ConstructorJotoInfo res = null;
		for(ConstructorJotoInfo elt : constructorInfos) {
			if (!elt.isPublic()) {
				continue;
			}
			if (!elt.isParamToFieldInfosComplete()) {
				continue;
			}
			res = elt; // found one!
			break; 
		}
		return res;
	}
	
}
