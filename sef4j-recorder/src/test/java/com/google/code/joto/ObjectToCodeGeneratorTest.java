package com.google.code.joto;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.joto.ast.beanstmt.BeanAST.BeanStmt;
import com.google.code.joto.ast.beanstmt.impl.BeanASTPrettyPrinter;
import com.google.code.joto.testobj.A;
import com.google.code.joto.testobj.Pt;
import com.google.code.joto.testobj.SimpleIntFieldA;
import com.google.code.joto.testobj.SimpleRefBean;
import com.google.code.joto.testobj.SimpleRefObjectFieldA;
import com.google.code.joto.testobj.TestObjFactory;


/**
 * JUnit test for ObjectToCodeGenerator 
 */
public class ObjectToCodeGeneratorTest {

	private static boolean DEBUG = true;
	private static Logger log = LoggerFactory.getLogger(ObjectToCodeGeneratorTest.class);
	
	@Test
	public void test_SimpleIntFieldA() {
		SimpleIntFieldA a = TestObjFactory.createSimpleIntFieldA();
		doTest("test_SimpleIntFieldA", a);
	}

	@Test
	public void test_SimpleRefObjectFieldA() {
		SimpleRefObjectFieldA a = TestObjFactory.createSimpleRefObjectFieldA();
		doTest("test_SimpleRefObjectFieldA", a);
	}

	@Test
	public void test_A() {
		A a = TestObjFactory.createBeanA();
		doTest("test_A", a);
	}
	
	@Test
	public void test_A2() {
		A a = TestObjFactory.createBeanA2();
		doTest("test_A2", a);
	}

	@Test
	public void test_SimpleRefA() {
		Object a = TestObjFactory.createSimpleRefA();
		doTest("test_SimpleRefA", a);
	}

	@Test
	public void test_SimpleRefBeanCyclic() {
		SimpleRefBean a = TestObjFactory.createSimpleRefBean_Cyclic();
		doTest("test_SimpleRefBeanCyclic", a);
	}

	@Test
	public void test_Pt() {
		Object a = new Pt(1, 2);
		doTest("test_Pt", a);
	}

	@Test
	public void test_ArrayListA() {
		A a = TestObjFactory.createBeanA();
		List<Object> ls = new ArrayList<Object>();
		ls.add(a);
		ls.add(a);
		doTest("test_ListA", ls);
	}
	
	// ------------------------------------------------------------------------
	
	private void doTest(String testName, Object obj) {
		if (DEBUG) {
			log.info(testName + " ...");
		}
		
		ObjectToCodeGenerator v2j = new ObjectToCodeGenerator();
		// v2j.setDebug(DEBUG);
		// v2j.setDebugValueHolder(DEBUG);
		// v2j.setDebugLinksFromValueHolder(DEBUG);

		List<BeanStmt> stmts = v2j.objToStmts(obj, "a");
		
		ByteArrayOutputStream bout = new ByteArrayOutputStream(); 
		PrintStream printStream = new PrintStream(bout); 
		BeanASTPrettyPrinter stmtPrinter = new BeanASTPrettyPrinter(printStream); 
		stmtPrinter.visitStmtList(stmts);
		if (DEBUG) {
			log.info("code={\n" + bout.toString() + "\n }");
		}
		
		if (DEBUG) {
			log.info("... done " + testName);
		}
	}


}
