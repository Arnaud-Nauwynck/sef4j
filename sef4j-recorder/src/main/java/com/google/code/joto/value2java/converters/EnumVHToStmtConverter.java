package com.google.code.joto.value2java.converters;

import com.google.code.joto.ast.beanstmt.BeanAST.BeanExpr;
import com.google.code.joto.ast.beanstmt.BeanAST.FieldExpr;
import com.google.code.joto.ast.beanstmt.BeanAST.LiteralExpr;
import com.google.code.joto.ast.beanstmt.BeanAST.SimpleNameExpr;
import com.google.code.joto.ast.valueholder.ValueHolderAST.AbstractObjectValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.FieldValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.ImmutableObjectValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.ObjectValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.RefFieldValueHolder;
import com.google.code.joto.value2java.ObjectVHToStmtConverter;
import com.google.code.joto.value2java.VHToStmt;
import com.google.code.joto.value2java.impl.ObjectStmtInfo;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * converter for java 5 Enum objects
 * 
 * format as  <<SimpleClassName>>.<<EnumName>>
 * where <<EnumName>> is retreived from field "java.lang.Enum#name" 
 *
 */
public class EnumVHToStmtConverter implements ObjectVHToStmtConverter {

	@Override
	public boolean canConvert(Class<?> type) {
		return type.isEnum();
	}

	@Override
	public void convert(VHToStmt owner, AbstractObjectValueHolder obj, ObjectStmtInfo objInfo) {
		try {
			ObjectValueHolder objVH = (ObjectValueHolder) obj;
			Class<?> enumClass = objVH.getObjClass();
	
			Class<?> enumLangClass = java.lang.Enum.class;
			Field[] enumLangFields = enumLangClass.getDeclaredFields();
			Field enumLangNameField = enumLangFields[0];
	
			Map<Field, FieldValueHolder> fieldsValuesMap = objVH.getFieldsValuesMap();
			RefFieldValueHolder enumLangNameRefVH = (RefFieldValueHolder) fieldsValuesMap.get(enumLangNameField);
			ImmutableObjectValueHolder enumLangNameVH = (ImmutableObjectValueHolder) enumLangNameRefVH.getTo();
			String enumLangNameValue = (String) enumLangNameVH.getValue();
			
			BeanExpr enumClassExpr = new SimpleNameExpr(enumClass.getSimpleName());
			FieldExpr enumFieldExpr = new FieldExpr(enumClassExpr, enumLangNameValue);
			
			objInfo.setTypeAndInitExpr(enumClass, enumFieldExpr);
		} catch(Exception ex) {
			objInfo.setTypeAndInitExpr(String.class, new LiteralExpr("*** Failed to decode enum ****"));
		}
	}

	
}
