package com.google.code.joto.value2java.converters;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.google.code.joto.ast.beanstmt.BeanAST.BeanExpr;
import com.google.code.joto.ast.beanstmt.BeanAST.ClassExpr;
import com.google.code.joto.ast.beanstmt.BeanAST.LiteralExpr;
import com.google.code.joto.ast.beanstmt.BeanAST.MethodApplyExpr;
import com.google.code.joto.ast.beanstmt.BeanAST.NewArrayExpr;
import com.google.code.joto.ast.valueholder.ValueHolderAST.AbstractObjectValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.ImmutableObjectValueHolder;
import com.google.code.joto.reflect.ReflectUtils;
import com.google.code.joto.value2java.ObjectVHToStmtConverter;
import com.google.code.joto.value2java.VHToStmt;
import com.google.code.joto.value2java.VHToStmtConverterLookup;
import com.google.code.joto.value2java.impl.ObjectStmtInfo;

/**
 * converters for ObjectValueHolder to Stmt,
 * representing internal (immutable) jre objects: java.lang. / java.lang.reflect objects..
 */
public class JavaLangVHToStmtConverters {

	private static final StringConverter StringConverterInstance = new StringConverter();
	private static final ClassConverter ClassConverterInstance = new ClassConverter();
	private static final MethodConverter MethodConverterInstance = new MethodConverter();
	
	public static void registerDefaultConverters(VHToStmtConverterLookup p) {
		p.registerConverter(StringConverterInstance, 0);
		p.registerConverter(ClassConverterInstance, 0);
		p.registerConverter(MethodConverterInstance, 0);
	}

	// -------------------------------------------------------------------------

	public static abstract class AbstractJavaLangVHToStmtConverter implements ObjectVHToStmtConverter {
		private final Class<?> type;

		protected AbstractJavaLangVHToStmtConverter(Class<?> type) {
			super();
			this.type = type;
		}

		@Override
		public boolean canConvert(Class<?> type) {
			return this.type == type;
		}

		@Override
		public void convert(VHToStmt owner, AbstractObjectValueHolder obj,
				ObjectStmtInfo objInfo) {
			if (!(obj instanceof ImmutableObjectValueHolder)) {
				throw new IllegalArgumentException();
			}
			ImmutableObjectValueHolder vh = (ImmutableObjectValueHolder) obj;
			Object value = vh.getValue();
			BeanExpr expr = doConvertValue(owner, obj, objInfo, value);
			objInfo.setTypeAndInitExpr(value.getClass(), expr);
		}

		protected abstract BeanExpr doConvertValue(VHToStmt owner,
				AbstractObjectValueHolder obj, ObjectStmtInfo objInfo,
				Object value);

		@Override
		public String toString() {
			String simpleName = type.getSimpleName(); 
			return simpleName + "VHToStmtConverter[]";
		}

	}

	/**
	 *
	 */
	public static class StringConverter extends AbstractJavaLangVHToStmtConverter {
		protected StringConverter() {
			super(String.class);
		}

		@Override
		protected BeanExpr doConvertValue(VHToStmt owner,
				AbstractObjectValueHolder obj, ObjectStmtInfo objInfo,
				Object value) {
			return new LiteralExpr(value);
		}

	}

	/**
	 *
	 */
	public static class ClassConverter extends AbstractJavaLangVHToStmtConverter {

		protected ClassConverter() {
			super(Class.class);
		}

		@Override
		protected BeanExpr doConvertValue(VHToStmt owner,
				AbstractObjectValueHolder obj, ObjectStmtInfo objInfo,
				Object value) {
			Class<?> clss = (Class<?>) value;
			String simpleName = clss.getSimpleName(); // todo use fully qualified name instead??
			return new ClassExpr(simpleName);
		}

	}


	/**
	 *
	 */
	public static class MethodConverter extends AbstractJavaLangVHToStmtConverter {

		protected MethodConverter() {
			super(Method.class);
		}

		@Override
		protected BeanExpr doConvertValue(VHToStmt owner,
				AbstractObjectValueHolder obj, ObjectStmtInfo objInfo,
				Object value) {
			Method meth = (Method) value;
			Class<?> declClass = meth.getDeclaringClass();
			String methodName = meth.getName();

			// test if method name is unique in parent class to generate simple resolver code
			List<Method> methodsByName = ReflectUtils.findMethodsByName(declClass, methodName, false);
			boolean isUniqByName = (methodsByName.size() == 1);
			List<BeanExpr> args = new ArrayList<BeanExpr>();
			args.add(new LiteralExpr(methodName));
			if (!isUniqByName) {
				// not uniq => <<className>>.class.getMethod(<<methodName>>, new Class[] { type0, type1, ...typeN});
				Class<?>[] paramTypes = meth.getParameterTypes();
				BeanExpr[] paramTypeExprs = new BeanExpr[paramTypes.length];
				for (int i = 0; i < paramTypes.length; i++) {
					String paramTypeName = paramTypes[i].getSimpleName(); // TOCHECK use simple name...
					paramTypeExprs[i] = new ClassExpr(paramTypeName); 
				}
				args.add(new NewArrayExpr(Class.class, paramTypes.length, paramTypeExprs));
			} // else uniq => <<className>>.class.getMethod(<<methodName>>);

			String simpleDeclaringClassName = declClass.getSimpleName(); // todo use fully qualified name instead??
			return new MethodApplyExpr(new ClassExpr(simpleDeclaringClassName),
					"getMethod", args);
		}

	}

}
