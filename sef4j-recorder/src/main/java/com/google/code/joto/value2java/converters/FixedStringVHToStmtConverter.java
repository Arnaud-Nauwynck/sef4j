package com.google.code.joto.value2java.converters;

import com.google.code.joto.ast.beanstmt.BeanAST.BeanExpr;
import com.google.code.joto.ast.beanstmt.BeanAST.SimpleNameExpr;
import com.google.code.joto.ast.valueholder.ValueHolderAST.AbstractObjectValueHolder;
import com.google.code.joto.value2java.ObjectVHToStmtConverter;
import com.google.code.joto.value2java.VHToStmt;
import com.google.code.joto.value2java.impl.ObjectStmtInfo;

/**
 * converter for simple Object->FixedString conversions, when matching a class 
 *
 */
public class FixedStringVHToStmtConverter implements ObjectVHToStmtConverter {

	private Class<Object> matchesType;
	private String resultText;
	
	// ------------------------------------------------------------------------

	public FixedStringVHToStmtConverter(Class<Object> matchesType, String resultText) {
		this.matchesType = matchesType;
		this.resultText = resultText;
	}

	public FixedStringVHToStmtConverter() {
	}

	// ------------------------------------------------------------------------
	
	@Override
	public boolean canConvert(Class<?> type) {
		if (matchesType == null) return false;
		return matchesType.isAssignableFrom(type);
	}

	@Override
	public void convert(VHToStmt owner, AbstractObjectValueHolder obj, ObjectStmtInfo objInfo) {
		BeanExpr expr = new SimpleNameExpr(resultText);
		objInfo.setTypeAndInitExpr(String.class, expr);
	}
	
}
