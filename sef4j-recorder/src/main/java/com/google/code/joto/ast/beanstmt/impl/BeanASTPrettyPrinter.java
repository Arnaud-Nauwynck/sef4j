package com.google.code.joto.ast.beanstmt.impl;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;

import com.google.code.joto.ast.beanstmt.BeanASTVisitor;
import com.google.code.joto.ast.beanstmt.BeanAST.AssignExpr;
import com.google.code.joto.ast.beanstmt.BeanAST.BeanExpr;
import com.google.code.joto.ast.beanstmt.BeanAST.BeanStmt;
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
 * 
 */
public class BeanASTPrettyPrinter implements BeanASTVisitor {


	private PrintStream out;
	
	private int indent;
	
	// ------------------------------------------------------------------------
	
	public BeanASTPrettyPrinter(PrintStream out) {
		this.out = out;
	}

	// implements BeanInitVisitor Statements
	// ------------------------------------------------------------------------
	
	public void caseBlock(BlockStmt p) {
		indentPrintln("{");
		incrIndent();
		for(BeanStmt stmt : p.getStmts()) {
			stmt.visit(this);
		}
		decrIndent();
		indentPrintln("}");
	}


	public void caseVarDecl(VarDeclStmt stmt) {
		Class<?> declType = stmt.getDeclaredType();
		String simpleClassName = classToSimpleName(declType); 
		indentPrint(simpleClassName + " " + stmt.getVarName());
		if (stmt.getInitExpr() != null) {
			print(" = ");
			stmt.getInitExpr().visit(this);
		}
		println(";");
	}

	public void caseExprStmt(ExprStmt p) {
		printIndent();
		p.getExpr().visit(this);
		println(";");
	}

	// implements BeanInitVisitor Expressions
	// ------------------------------------------------------------------------
	
	public void caseAssign(AssignExpr p) {
		p.getLhs().visit(this);
		print(" = ");
		p.getRhs().visit(this);
	}

	public void caseMethodApplyExpr(MethodApplyExpr p) {
		if (p.getLhsExpr() != null) {
			p.getLhsExpr().visit(this);
			print(".");
		}
		print(p.getMethodName());
		print("(");
		visitExprList(p.getArgs());
		print(")");
	}
	
	public void caseNewObject(NewObjectExpr p) {
		print("new ");
		print(classToSimpleName(p.getNewClss()));
		print("(");
		visitExprList(p.getArgs());
		print(")");
	}

	public void caseNewArray(NewArrayExpr p) {
		print("new ");
		print(classToSimpleName(p.getNewArrayClass()));
		print("[");
		BeanExpr[] initExprs = p.getInitExprs();
		if (initExprs == null || p.getLength() != p.getInitExprs().length) {
			print(Integer.toString(p.getLength()));
		}
		print("]");
		if (initExprs != null) {
			print(" { ");
			visitExprs(initExprs);
			print(" }");
		}
	}

	public void caseIndexedArray(IndexedArrayExpr p) {
		p.getLhs().visit(this);
		print("[");
		p.getIndex().visit(this);
		print("]");
	}

	public void caseLitteralExpr(LiteralExpr p) {
		String javaValue;
		Object value = p.getValue();
		javaValue = litteralToJava(value);
		print(javaValue);
	}

	public static String litteralToJava(Object value) {
		String javaValue;
		if (value == null) {
			javaValue = "null";
		} else if (value instanceof String) {
			String str = (String) value;
			javaValue = litteralStringToJavaCode(str);
		} else if (value instanceof Character) {
			// if ()
			char ch = ((Character) value).charValue();
			javaValue = litteralCharToJavaCode(ch);
		} else {
			javaValue = value.toString(); // TODO format...
		}
		return javaValue;
	}

	public static String litteralStringToJavaCode(String str) {
		String javaValue;
		str = str.replace("\\", "\\\\"); // replace '\' by '\\'
		str = str.replace("\"", "\\\""); // replace '"' by '\"'
		str = str.replace("\n", "\\n"); // replace <newline> by '\n'
		// TODO add more escape..
		javaValue = "\"" + str + "\"";
		return javaValue;
	}

	public static String litteralCharToJavaCode(char ch) {
		String javaValue;
		String str = Character.toString(ch);
		if (ch == '\\') { str = "\\\\"; }
		else if (ch == '\'') { str = "\\\'"; }
		else if (ch == '\0') { str = "\\0"; }
		else if (ch == '\n') { str = "\\n"; }
		// TODO add more escape..
		javaValue = "'" + str + "'";
		return javaValue;
	}


	public void caseClassExpr(ClassExpr p) {
		print(p.getLhsClassName());
		print(".class");
	}


	public void caseFieldExpr(FieldExpr p) {
		p.getLhs().visit(this);
		print(".");
		print(p.getFieldName());
	}
	
	public void caseSimpleName(SimpleNameExpr p) {
		print(p.getName());
	}

	// ------------------------------------------------------------------------

	public void visitStmtList(List<BeanStmt> stmts) {
		for(BeanStmt stmt : stmts) {
			stmt.visit(this);
		}
	}
	
	protected void visitExprList(List<BeanExpr> args) {
		if (args != null && !args.isEmpty()) {
			for (Iterator<BeanExpr> iter = args.iterator(); iter.hasNext();) {
				BeanExpr arg = iter.next();
				arg.visit(this);
				if (iter.hasNext()) {
					print(", ");
				}
			}
		}
	}

	protected void visitExprs(BeanExpr[] args) {
		if (args != null && args.length != 0) {
			for (int i = 0, len = args.length; i < len; i++) {
				BeanExpr arg = args[i];
				if (arg != null) {
					arg.visit(this);
				}
				if (i + 1 < len) {
					print(", ");
				}
			}
		}
	}

	protected String classToSimpleName(Class<?> p) {
		if (p == null) {
			p = Object.class; // should not occur!
		}
		String res = p.getSimpleName();
		// TODO add import..
		if (-1 != res.indexOf('$')) {
			res = res.replace('$', '.'); // TODO add static inner class import?
		}
		return res;
	}
	
	// -------------------------------------------------------------------------
	
	public void incrIndent() {
		indent++;
	}

	public void decrIndent() {
		indent--;
	}
	public void printIndent() {
		for (int i = 0; i < indent; i++) {
			out.print(' ');
		}
	}

	private void print(String text) {
		out.print(text);
	}

	private void println() {
		out.print('\n');
	}

	private void println(String text) {
		print(text);
		println();
	}

	private void indentPrint(String text) {
		printIndent();
		print(text);
	}
	
	private void indentPrintln(String text) {
		printIndent();
		print(text);
		println();
	}

}
