package com.google.code.joto.util.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.google.code.joto.util.JotoRuntimeException;

/**
 * reflection utility class to bind swing listeners (inner classes replacements) to object method invocation
 *
 */
public class SwingBinderUtils {
	
	public static ActionListener createObjectActionListener(Object targetObj, String methodName) {
		Class<?> targetClass = targetObj.getClass();
		Class<?>[] argTypes = new Class[] { ActionEvent.class };
		Method meth;
		try {
			meth = targetClass.getMethod(methodName, argTypes);
		} catch (SecurityException ex) {
			throw JotoRuntimeException.wrapRethrow("Can not find method " + targetClass.getSimpleName() + "." + methodName + "ActionEvent evt)", ex);
		} catch (NoSuchMethodException ex) {
			throw JotoRuntimeException.wrapRethrow("Method not found " + targetClass.getSimpleName() + "." + methodName + "(ActionEvent evt)", ex);
		}
		if (meth == null) {
			throw new JotoRuntimeException("method not found");
		}
		ObjectReflectActionListener res = new ObjectReflectActionListener(targetObj, meth);
		return res;
	}

	/**
	 *
	 */
	public static class ObjectReflectActionListener implements ActionListener {
		Object target;
		Method meth;
		
		public ObjectReflectActionListener(Object target, Method meth) {
			this.target = target;
			this.meth = meth;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Object[] args = new Object[] { e };
			try {
				meth.invoke(target, args);
			} catch (IllegalArgumentException ex) {
				throw new JotoRuntimeException("Failed to invoke method", ex);
			} catch (IllegalAccessException ex) {
				throw new JotoRuntimeException("Failed to invoke method", ex);
			} catch (InvocationTargetException ex) {
				throw new JotoRuntimeException("Failed to invoke method", ex);
			}
		}
		
	}
}
