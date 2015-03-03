package com.google.code.joto.value2java.impl;

import java.util.ArrayList;
import java.util.List;

import com.google.code.joto.ast.beanstmt.BeanAST.BeanExpr;
import com.google.code.joto.ast.beanstmt.BeanAST.BeanStmt;
import com.google.code.joto.ast.beanstmt.BeanAST.VarDeclStmt;
import com.google.code.joto.ast.valueholder.ValueHolderAST.AbstractObjectValueHolder;

/**
 *
 */
public class ObjectStmtInfo {
	
	private AbstractObjectValueHolder objectVH;
	
	private VarDeclStmt varDeclStmt = new VarDeclStmt(null, null, null);
	
	private List<BeanStmt> initStmts = new ArrayList<BeanStmt>();
	
	// -------------------------------------------------------------------------
	
	public ObjectStmtInfo(AbstractObjectValueHolder objectVH) {
		this.objectVH = objectVH;
	}

	// -------------------------------------------------------------------------

	public AbstractObjectValueHolder getObjectVH() {
		return objectVH;
	}

	public String getVarName() {
		return varDeclStmt.getVarName();
	}

	public void setVarName(String p) {
		varDeclStmt.setVarName(p);
	}

	public String getVarNameWithSuffix(String suffix) {
		String tmp = varDeclStmt.getVarName();
		return (tmp != null)? tmp + suffix : suffix;
	}

	public VarDeclStmt getVarDeclStmt() {
		return varDeclStmt;
	}
	
	public List<BeanStmt> getInitStmts() {
		return initStmts;
	}

	public void addInitStmt(BeanStmt p) {
		initStmts.add(p);
	}

	public void setTypeAndInitExpr(Class<?> declaredType, BeanExpr initExpr) {
		varDeclStmt.setDeclaredType(declaredType);
		varDeclStmt.setInitExpr(initExpr);
	}
}