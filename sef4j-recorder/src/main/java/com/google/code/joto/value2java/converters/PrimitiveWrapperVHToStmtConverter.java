package com.google.code.joto.value2java.converters;

import com.google.code.joto.ast.beanstmt.BeanAST.BeanExpr;
import com.google.code.joto.ast.beanstmt.BeanAST.LiteralExpr;
import com.google.code.joto.ast.beanstmt.BeanAST.MethodApplyExpr;
import com.google.code.joto.ast.beanstmt.BeanAST.SimpleNameExpr;
import com.google.code.joto.ast.valueholder.ValueHolderAST.AbstractObjectValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.FieldValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.ImmutableObjectValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.ObjectValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.PrimitiveFieldValueHolder;
import com.google.code.joto.value2java.ObjectVHToStmtConverter;
import com.google.code.joto.value2java.VHToStmt;
import com.google.code.joto.value2java.VHToStmtConverterLookup;
import com.google.code.joto.value2java.impl.ObjectStmtInfo;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * Converter for ObjectValueHolder -> Stmt , for primitive wrapper 
 * example:
 *    object value: AbstractObjectValueHolder... for "java.lang.Integer"  
 *    => convert to BeanExpr(no additional stmts)... for "Integer.valueOf(primitiveIntValue)"
 * 
 */
public class PrimitiveWrapperVHToStmtConverter implements ObjectVHToStmtConverter {

    private static final PrimitiveWrapperVHToStmtConverter BooleanConv = new PrimitiveWrapperVHToStmtConverter(Boolean.class); 
    private static final PrimitiveWrapperVHToStmtConverter CharacterConv = new PrimitiveWrapperVHToStmtConverter(Character.class);
    private static final PrimitiveWrapperVHToStmtConverter ByteConv = new PrimitiveWrapperVHToStmtConverter(Byte.class);
    private static final PrimitiveWrapperVHToStmtConverter ShortConv = new PrimitiveWrapperVHToStmtConverter(Short.class);
    private static final PrimitiveWrapperVHToStmtConverter IntegerConv = new PrimitiveWrapperVHToStmtConverter(Integer.class);
    private static final PrimitiveWrapperVHToStmtConverter LongConv = new PrimitiveWrapperVHToStmtConverter(Long.class);
    private static final PrimitiveWrapperVHToStmtConverter FloatConv = new PrimitiveWrapperVHToStmtConverter(Float.class);
    private static final PrimitiveWrapperVHToStmtConverter DoubleConv = new PrimitiveWrapperVHToStmtConverter(Double.class);

    private static final EnumVHToStmtConverter EnumConv = new EnumVHToStmtConverter();

    public static void registerDefaultConverters(VHToStmtConverterLookup p) {
    	p.registerConverter(BooleanConv, 0);
    	p.registerConverter(CharacterConv, 0);
    	p.registerConverter(ByteConv, 0);
    	p.registerConverter(ShortConv, 0);
    	p.registerConverter(IntegerConv, 0);
    	p.registerConverter(LongConv, 0);
    	p.registerConverter(FloatConv, 0);
    	p.registerConverter(DoubleConv, 0);

    	p.registerConverter(EnumConv, 0);
    
    }
	
//	private final Class<?> primitiveType;
	private final Class<?> wrapperType;
	
	//-------------------------------------------------------------------------

	protected PrimitiveWrapperVHToStmtConverter(Class<?> wrapperType) {
//		this.primitiveType = ReflectUtils.wrapperTypeToPrimitive(wrapperType);
		this.wrapperType = wrapperType; 
	}

	// -------------------------------------------------------------------------
	
	@Override
	public boolean canConvert(Class<?> type) {
		return wrapperType == type;
	}

	@Override
	public void convert(VHToStmt owner, 
			AbstractObjectValueHolder obj, ObjectStmtInfo objInfo) {
		// step1: extract value from valueHolder
		Object wrapperValue;
		Class<?> wrapperType;
		if (obj instanceof ImmutableObjectValueHolder) {
			ImmutableObjectValueHolder obj2 = (ImmutableObjectValueHolder) obj;
			wrapperValue = obj2.getValue();
			wrapperType = obj2.getObjClass();
		} else if (obj instanceof ObjectValueHolder) {
			ObjectValueHolder obj2 = (ObjectValueHolder) obj;
			// object should have only 1 field, using PrimitiveFieldValueHolder..
			Map<Field, FieldValueHolder> fields = obj2.getFieldsValuesMap();
			if (fields.size() != 1) throw new IllegalStateException();
			FieldValueHolder fVH = fields.values().iterator().next();
			if (!(fVH instanceof PrimitiveFieldValueHolder)) throw new IllegalStateException();
			wrapperValue = ((PrimitiveFieldValueHolder) fVH).getValue();
			wrapperType = obj2.getObjClass();
		} else {
			throw new IllegalStateException();
		}
		assert wrapperValue.getClass() == wrapperType;
		
		// convert value from wrapper, to stmt		
		String wrapperTypeName = wrapperType.getClass().getSimpleName();
		String methodName = "valueOf";

		BeanExpr initExpr = new MethodApplyExpr(
				new SimpleNameExpr(wrapperTypeName), 
				methodName, 
				new LiteralExpr(wrapperValue));

		objInfo.setTypeAndInitExpr(wrapperType, initExpr);
	}

	// override java.lang.Object
	//-------------------------------------------------------------------------

	@Override
	public String toString() {
		return "PrimitiveWrapperVHToStmtConverter[" + wrapperType + "]";
	}
	
}
