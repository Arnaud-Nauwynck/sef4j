package com.google.code.joto.reflect;

import java.beans.ConstructorProperties;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;

/**
 * 
 */
public class ConstructorJotoInfo extends MemberJotoInfo {

	private static Logger log = LoggerFactory.getLogger(ConstructorJotoInfo.class);

	private final Constructor<?> targetConstructor;

	private List<ParamToFieldInfo> paramToFieldInfos = new ArrayList<ParamToFieldInfo>();  
	
	// -------------------------------------------------------------------------
	
	public ConstructorJotoInfo(ClassJotoInfo parent, Constructor<?> targetConstructor) {
		super(parent);
		this.targetConstructor = targetConstructor;

		Class<?>[] paramTypes = targetConstructor.getParameterTypes();
		int paramLength = paramTypes.length;
		if (paramLength != 0) {
			Field[] paramToFields = new Field[paramLength];
			
			boolean resolve = resolveNonAmbiguousFields(paramToFields);
			if (!resolve) {
				resolveInfosUsingConstructorPropertiesAnnotation(paramToFields);
			}
			
			for(int i = 0; i < paramLength; i++) {
				paramToFieldInfos.add(new ParamToFieldInfo(paramTypes[i], paramToFields[i])); 
			}
		}
	}

	// -------------------------------------------------------------------------

	public Constructor<?> getTargetConstructor() {
		return targetConstructor;
	}

	public List<ParamToFieldInfo> getParamToFieldInfos() {
		return paramToFieldInfos;
	}

	public boolean isPublic() {
		return // targetConstructor.isAccessible() &&
			Modifier.isPublic(targetConstructor.getModifiers());
	}
	
	public boolean isParamToFieldInfosComplete() {
		int paramLenght = targetConstructor.getParameterTypes().length;
		if (paramLenght == 0) {
			return true;
		}
		if (paramToFieldInfos == null || paramToFieldInfos.size() != paramLenght) {
			return false;
		}
		boolean res = true;
		for(ParamToFieldInfo paramToFieldInfo : paramToFieldInfos) {
			if (paramToFieldInfo == null || paramToFieldInfo.getTargetAssignedField() == null) {
				res = false;
				break;
			}
		}
		return res;
	}

	
	private boolean resolveNonAmbiguousFields(Field[] paramToFields) {
		boolean res = true;
		Class<?> targetClass = parent.getTargetClass();
		Class<?>[] paramTypes = targetConstructor.getParameterTypes();
		int len = paramTypes.length;
		for(int i = 0; i < len; i++) {
			Class<?> paramType = paramTypes[i];
			// find if type is assignable to a field, without ambiguity
			List<Field> assignableFields = ReflectUtils.findAssignableFieldsForValueType(targetClass, paramType);
			if (assignableFields.size() == 1) {
				// ok, found a non ambiguous field
				paramToFields[i] = assignableFields.get(0); 
			} else {
				res = false; // at least one not found / ambiguous field
			}
		}
		return res;
	}
	
	private boolean resolveInfosUsingConstructorPropertiesAnnotation(Field[] paramToFields) {
		boolean res = false;
		ConstructorProperties ctorPropAnnotation = (ConstructorProperties) 
			targetConstructor.getAnnotation(ConstructorProperties.class);
		if (ctorPropAnnotation != null) {
			res = true;
			String[] ctorPropValues = ctorPropAnnotation.value();
			Class<?> targetClass = parent.getTargetClass();
			ReflectionProvider rp = ReflectUtils.getReflectionProvider();
			for(int i = 0; i < ctorPropValues.length; i++) {
				String ctorPropValue = ctorPropValues[i];
				Field f;
				try {
					f = rp.getField(targetClass, ctorPropValue);
				} catch(Exception ex) {
					log.warn("Failed to get field for ctor parameter annotation: " + targetClass 
							+ ", param" + i + " -> field? " + ctorPropValue, ex);
					f = null;
				}
				if (f == null) {
					res = false;
				}
				paramToFields[i] = f;
			}
		} // else annotation not found!
		return res;
	}

}
