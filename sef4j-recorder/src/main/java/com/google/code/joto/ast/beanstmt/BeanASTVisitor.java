package com.google.code.joto.ast.beanstmt;

import com.google.code.joto.ast.beanstmt.BeanAST.AssignExpr;
import com.google.code.joto.ast.beanstmt.BeanAST.BlockStmt;
import com.google.code.joto.ast.beanstmt.BeanAST.ClassExpr;
import com.google.code.joto.ast.beanstmt.BeanAST.ExprStmt;
import com.google.code.joto.ast.beanstmt.BeanAST.FieldExpr;
import com.google.code.joto.ast.beanstmt.BeanAST.IndexedArrayExpr;
import com.google.code.joto.ast.beanstmt.BeanAST.LiteralExpr;
import com.google.code.joto.ast.beanstmt.BeanAST.MethodApplyExpr;
import com.google.code.joto.ast.beanstmt.BeanAST.NewArrayExpr;
import com.google.code.joto.ast.beanstmt.BeanAST.NewObjectExpr;
import com.google.code.joto.ast.beanstmt.BeanAST.VarDeclStmt;
import com.google.code.joto.ast.beanstmt.BeanAST.SimpleNameExpr;

/**
 * Visitor design pattern, for BeanAST class hierarchy
 * 
 * See also BeanASTVisitor2 with arg/return. 
 */
public interface BeanASTVisitor {

	void caseExprStmt(ExprStmt p);

	void caseAssign(AssignExpr p);
	void caseMethodApplyExpr(MethodApplyExpr p);
	void caseLitteralExpr(LiteralExpr p);
	void caseNewObject(NewObjectExpr p);
	void caseNewArray(NewArrayExpr p);
	void caseIndexedArray(IndexedArrayExpr p);
	void caseClassExpr(ClassExpr p);
	void caseFieldExpr(FieldExpr p);
	void caseSimpleName(SimpleNameExpr p);

	void caseVarDecl(VarDeclStmt stmt);
	void caseBlock(BlockStmt stmt);

}
