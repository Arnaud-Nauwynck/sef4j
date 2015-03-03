package com.google.code.joto.ast.beanstmt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.code.joto.ast.beanstmt.impl.BeanASTToStringFormatter;
import com.google.code.joto.util.attr.DefaultAttributeSupport;
import com.google.code.joto.util.attr.IAttributeSupport;
import com.google.code.joto.util.attr.IAttributeSupportDelegate;

/**
 * AST for java code relative to javabean handling.<BR/>
 * very simplified AST sub-part of the java langage! 
 * <p/>
 * 
 * Typically, it allows writing code like
 * <pre>
 *   MyClass bean = new MyClass();
 *   bean.setField1(123);
 *   bean.addElt(new MyClassElement());
 * </pre>
 * 
 * Such code can be created programmatically by
 * <pre>
 *   // line 1: "MyClass bean = new MyClass();"
 *   VarDeclStmt beanVarDecl = new VarDeclStmt(MyClass.class, "bean", new NewObjectExpr(MyClass.class));
 *   
 *   // line 2: "bean.setField1(123);"
 *   BeanExpr expr2 = new MethodApplyExpr(new SimpleNameExpr("bean"), "setField1", singletonList(new LiteralExpr(123)));
 *   ExprStmt stmt2 = new ExprStmt(expr2);
 *   
 *   // line 3: "bean.addElt(new MyClassElement());"
 *   BeanExpr expr3 = new MethodApplyExpr(new SimpleNameExpr("bean"), "addElt", new NewObjectExpr(MyClassElement.class));
 *   ExprStmt stmt3 = new ExprStmt(expr3);
 * </pre>
 * 
 */
public abstract class BeanAST implements IAttributeSupportDelegate {

	private IAttributeSupport attributeSupport;
	

	public BeanAST() {
	}
	
	public abstract void visit(BeanASTVisitor v);
	public abstract <R,A> R visit(BeanASTVisitor2<R,A> v, A arg);

	public IAttributeSupport getAttributeSupport() {
		 if (attributeSupport == null) {
			 attributeSupport = new DefaultAttributeSupport();
		}
		 return attributeSupport;
	}

	@Override
	public String toString() {
		String javaText = BeanASTToStringFormatter.getInstance().objectToString(this);
		return javaText;
	}
	
	// -------------------------------------------------------------------------
	


	/**
	 *
	 */
	public static abstract class BeanStmt extends BeanAST {
		
	}

	/**
	 *
	 */
	public static abstract class BeanExpr extends BeanAST {
		
	}
	
	/**
	 *
	 */
	public static class ExprStmt extends BeanStmt {
		private BeanExpr beanExpr;

		public ExprStmt(BeanExpr beanExpr) {
			this.beanExpr = beanExpr;
		}

		public void visit(BeanASTVisitor v) {
			v.caseExprStmt(this);
		}
		
		public <R,A> R visit(BeanASTVisitor2<R,A> v, A arg) {
			return v.caseExprStmt(this, arg);
		}

		public BeanExpr getExpr() {
			return beanExpr;
		}
		
	}
	
	/**
	 *
	 */
	public static class AssignExpr extends BeanExpr {
		private BeanExpr lhs;
		private BeanExpr rhs;
		
		public AssignExpr(BeanExpr lhs, BeanExpr rhs) {
			this.lhs = lhs;
			this.rhs = rhs;
		}

		public void visit(BeanASTVisitor v) {
			v.caseAssign(this);
		}
		
		public <R,A> R visit(BeanASTVisitor2<R,A> v, A arg) {
			return v.caseAssign(this, arg);
		}

		public BeanExpr getLhs() {
			return lhs;
		}

		public BeanExpr getRhs() {
			return rhs;
		}

		public void setRhs(BeanExpr rhs) {
			this.rhs = rhs;
		}

		public void setLhs(BeanExpr lhs) {
			this.lhs = lhs;
		}
	}
	
	
	/**
	 * 
	 */
	public static class MethodApplyExpr extends BeanExpr {
		BeanExpr lhsExpr;
		String methodName;
		List<BeanExpr> args = new ArrayList<BeanExpr>();

		
		public MethodApplyExpr(BeanExpr lhsExpr, String methodName, List<BeanExpr> args) {
			this.lhsExpr = lhsExpr;
			this.methodName = methodName;
			this.args = args;
		}

		public MethodApplyExpr(BeanExpr lhsExpr, String methodName, BeanExpr... optArgs) {
			this.lhsExpr = lhsExpr;
			this.methodName = methodName;
			if (optArgs != null) {
				args.addAll(Arrays.asList(optArgs));
			}
		}

		public void visit(BeanASTVisitor v) {
			v.caseMethodApplyExpr(this);
		}

		public <R,A> R visit(BeanASTVisitor2<R,A> v, A arg) {
			return v.caseMethodApplyExpr(this, arg);
		}

		public BeanExpr getLhsExpr() {
			return lhsExpr;
		}

		public String getMethodName() {
			return methodName;
		}

		public List<BeanExpr> getArgs() {
			return args;
		}

	}
	
	/**
	 * 
	 */
	public static class LiteralExpr extends BeanExpr {
		Object value;

		public LiteralExpr(Object value) {
			super();
			this.value = value;
		}

		public void visit(BeanASTVisitor v) {
			v.caseLitteralExpr(this);
		}

		public <R,A> R visit(BeanASTVisitor2<R,A> v, A arg) {
			return v.caseLitteralExpr(this, arg);
		}

		public Object getValue() {
			return value;
		}
		
	}

	/**
	 * 
	 */
	public static class NewObjectExpr extends BeanExpr {
		Class<?> newClss;
		List<BeanExpr> args = new ArrayList<BeanExpr>();
		
		public NewObjectExpr(Class<?> clss) {
			this(clss, (List<BeanExpr>) null);
		}

		public NewObjectExpr(Class<?> clss, BeanExpr... args) {
			this(clss, Arrays.asList(args));
		}
		
		public NewObjectExpr(Class<?> clss, List<BeanExpr> args) {
			super();
			this.newClss = clss;
			if (args != null) {
				this.args.addAll(args);
			}
		}

		public void visit(BeanASTVisitor v) {
			v.caseNewObject(this);
		}

		public <R,A> R visit(BeanASTVisitor2<R,A> v, A arg) {
			return v.caseNewObject(this, arg);
		}

		public Class<?> getNewClss() {
			return newClss;
		}

		public List<BeanExpr> getArgs() {
			return args;
		}
		
	}

	/**
	 * 
	 */
	public static class NewArrayExpr extends BeanExpr {
		private Class<?> newArrayClass;
		private int length;
		private BeanExpr[] initExprs;
		
		public NewArrayExpr(Class<?> newArrayClass, int length) {
			super();
			this.newArrayClass = newArrayClass;
			this.length = length;
		}

		public NewArrayExpr(Class<?> newArrayClass, int length, BeanExpr[] initExprs) {
			super();
			this.newArrayClass = newArrayClass;
			this.length = length;
			this.initExprs = initExprs;
		}
		
		public void visit(BeanASTVisitor v) {
			v.caseNewArray(this);
		}
		
		public <R,A> R visit(BeanASTVisitor2<R,A> v, A arg) {
			return v.caseNewArray(this, arg);
		}

		public Class<?> getNewArrayClass() {
			return newArrayClass;
		}

		public int getLength() {
			return length;
		}

		public BeanExpr[] getInitExprs() {
			return initExprs;
		}
		
	}
	
	/**
	 * 
	 */
	public static class IndexedArrayExpr extends BeanExpr {
		private BeanExpr lhs;
		private BeanExpr index;
		
		public IndexedArrayExpr(BeanExpr lhs, BeanExpr index) {
			super();
			this.lhs = lhs;
			this.index = index;
		}

		@Override
		public void visit(BeanASTVisitor v) {
			v.caseIndexedArray(this);
		}

		@Override
		public <R, A> R visit(BeanASTVisitor2<R, A> v, A arg) {
			return v.caseIndexedArray(this, arg);
		}

		public BeanExpr getLhs() {
			return lhs;
		}

		public void setLhs(BeanExpr lhs) {
			this.lhs = lhs;
		}

		public BeanExpr getIndex() {
			return index;
		}

		public void setIndex(BeanExpr index) {
			this.index = index;
		}
		
	}
	
	/**
	 * 
	 */
	public static class ClassExpr extends BeanExpr {
		String lhsClassName;

		public ClassExpr(String lhsClassName) {
			super();
			this.lhsClassName = lhsClassName;
		}

		public void visit(BeanASTVisitor v) {
			v.caseClassExpr(this);
		}

		public <R,A> R visit(BeanASTVisitor2<R,A> v, A arg) {
			return v.caseClassExpr(this, arg);
		}

		public String getLhsClassName() {
			return lhsClassName;
		}
		
	}

	/**
	 * 
	 */
	public static class FieldExpr extends BeanExpr {
		private BeanExpr lhs;
		private String fieldName;
		
		public FieldExpr(BeanExpr lhs, String fieldName) {
			super();
			this.lhs = lhs;
			this.fieldName = fieldName;
		}

		public void visit(BeanASTVisitor v) {
			v.caseFieldExpr(this);
		}

		public <R,A> R visit(BeanASTVisitor2<R,A> v, A arg) {
			return v.caseFieldExpr(this, arg);
		}
		
		public BeanExpr getLhs() {
			return lhs;
		}

		public void setLhs(BeanExpr p) {
			this.lhs = p;
		}

		public void setFieldName(String p) {
			this.fieldName = p;
		}

		public String getFieldName() {
			return fieldName;
		}
		
	}

	/**
	 * 
	 */
	public static class SimpleNameExpr extends BeanExpr {
		private String name;
		// TODO can be null + should be more general: use "Symbol"  (either LocalVarDecl, ParameterDecl, Field, or Class...)
		private VarDeclStmt resolvedDecl; 
		
		public SimpleNameExpr(String p) {
			super();
			this.name = p;
		}

		public SimpleNameExpr(VarDeclStmt resolvedDecl) {
			super();
			this.resolvedDecl = resolvedDecl;
			this.name = resolvedDecl.getVarName();
		}

		public void visit(BeanASTVisitor v) {
			v.caseSimpleName(this);
		}

		public <R,A> R visit(BeanASTVisitor2<R,A> v, A arg) {
			return v.caseSimpleName(this, arg);
		}

		public String getName() {
			return name;
		}

		public VarDeclStmt getResolvedDecl() {
			return resolvedDecl;
		}
		
	}

	// -------------------------------------------------------------------------
	
	/**
	 * 
	 */
	public static class VarDeclStmt extends BeanStmt {
		
		private Class<?> declaredType;
		private String varName;
		private BeanExpr initExpr; 
		
		public VarDeclStmt(Class<?> declaredType, String varName, BeanExpr initExpr) {
			this.declaredType = declaredType;
			this.varName = varName;
			this.initExpr = initExpr;
		}
		
		public void visit(BeanASTVisitor v) {
			v.caseVarDecl(this);
		}

		public <R,A> R visit(BeanASTVisitor2<R,A> v, A arg) {
			return v.caseVarDecl(this, arg);
		}

		public Class<?> getDeclaredType() {
			return declaredType;
		}

		public void setDeclaredType(Class<?> declaredType) {
			this.declaredType = declaredType;
		}

		public String getVarName() {
			return varName;
		}

		public void setVarName(String p) {
			this.varName = p;
		}

		public BeanExpr getInitExpr() {
			return initExpr;
		}

		public void setInitExpr(BeanExpr p) {
			this.initExpr = p;
		}
		
	}
	
	/**
	 * 
	 */
	public static class BlockStmt extends BeanStmt {
		
		private List<BeanStmt> stmts = new ArrayList<BeanStmt>();
		
		public BlockStmt() {
		}
		
		public void visit(BeanASTVisitor v) {
			v.caseBlock(this);
		}
		
		public <R,A> R visit(BeanASTVisitor2<R,A> v, A arg) {
			return v.caseBlock(this, arg);
		}
		
		public void doVisitChildStmts(BeanASTVisitor v) {
			for(BeanStmt stmt : stmts) {
				stmt.visit(v);
			}
		}
		public <R,A> R doVisitChildStmts(BeanASTVisitor2<R,A> v, A arg) {
			for(BeanStmt stmt : stmts) {
				stmt.visit(v, arg);
			}
			return null;
		}
		
		public List<BeanStmt> getStmts() {
			return stmts;
		}

		public void addStmt(BeanStmt p) {
			stmts.add(p);
		}
		
		public void addExprStmt(BeanExpr p) {
			addStmt(new ExprStmt(p));
		}
		
	}
	
}
