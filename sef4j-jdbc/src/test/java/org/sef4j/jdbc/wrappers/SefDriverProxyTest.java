package org.sef4j.jdbc.wrappers;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sef4j.callstack.LocalCallStack;
import org.sef4j.callstack.handlers.Slf4jLoggerAdapterCallStackHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SefDriverProxyTest {

	private static final Logger LOG = LoggerFactory.getLogger(SefDriverProxyTest.class);
	
	static {
		try {
		     Class.forName("org.hsqldb.jdbc.JDBCDriver" );
		 } catch (Exception e) {
		     System.err.println("ERROR: failed to load HSQLDB JDBC driver.");
		 }
	}

	private static final String URL_DB1 = "jdbc:hsqldb:file:src/test/hsqldb1/db;create=true;";
	private Driver targetDriver = new org.hsqldb.jdbcDriver();
	private SefDriverProxy driverProxy = new SefDriverProxy(targetDriver);

	@BeforeClass
	public static void setup() {
		LocalCallStack.currThreadStackElt().addRootCallStackHandler(new Slf4jLoggerAdapterCallStackHandler(LOG));
	}
	
	@Test
	public void testConnect() throws SQLException {
		// Prepare
		// Perform
		Connection res = driverProxy.connect(URL_DB1, null);
		// Post-check
		Assert.assertNotNull(res);
		Assert.assertTrue(res instanceof SefConnectionProxy);
		SefConnectionProxy resProxy = (SefConnectionProxy) res;
		Connection targetCon = resProxy.getUnderlyingConnection();
		Assert.assertNotNull(targetCon);
		Assert.assertTrue(targetCon instanceof org.hsqldb.jdbc.JDBCConnection);
		Assert.assertFalse(targetCon.isClosed());
		
		// Prepare
		// Perform
		res.close();
		// Post-check
		Assert.assertTrue(targetCon.isClosed());
	}

	@Test
	public void testConnect_preparedStatement_SHUTDOWN() throws SQLException {
		// Prepare
		Connection res = driverProxy.connect(URL_DB1, null);
		// Perform
		executeSHUTDOWN(res);
		// Post-check
		res.close();
	}

	private void executeSHUTDOWN(Connection res) throws SQLException {
		String sql = "SHUTDOWN";
		PreparedStatement pstmt = res.prepareStatement(sql);
		pstmt.execute();
		pstmt.close();
	}

	
	@Test
	public void testConnect_preparedStatement_createTable() throws SQLException {
		// Prepare
		Connection conn = driverProxy.connect(URL_DB1, null);
		// Perform
		// cf http://jailer.sourceforge.net/scott-tiger.sql.html
		String sql = "CREATE TABLE if not exists EMPLOYEE("
				+ "   empno      INTEGER NOT NULL,"
				+ "   name       VARCHAR(10),"
				+ "   job        VARCHAR(9),"
				+ "   boss       INTEGER,"
				+ "   hiredate   VARCHAR(12),"
				+ "   salary     DECIMAL(7, 2),"
				+ "   comm       DECIMAL(7, 2),"
				+ "   deptno     INTEGER"
				+ ")";

		executePStatement(conn, sql);
		
		String sqlDep = "CREATE TABLE if not exists DEPARTMENT("
				+ "   deptno     INTEGER NOT NULL,"
				+ "   name       VARCHAR(14),"
				+ "   location   VARCHAR(13)"
				+ ")";

		executePStatement(conn, sqlDep);

		executeSHUTDOWN(conn);

		// Post-check
		conn.close();
	}

	
	@Test
	public void testConnect_preparedStatement_CRUD() throws SQLException {
		// Prepare
		Connection conn = driverProxy.connect(URL_DB1, null);
		// Perform
		int count = executePStatement_int(conn, "select count(*) from DEPARTMENT where deptno=10");
		if (count == 0) {
			executePStatement(conn, "INSERT INTO DEPARTMENT VALUES (10, 'ACCOUNTING', 'NEW YORK')");
		}
		
//		INSERT INTO DEPARTMENT VALUES (20, 'RESEARCH',   'DALLAS');
//		INSERT INTO DEPARTMENT VALUES (30, 'SALES',      'CHICAGO');
//		INSERT INTO DEPARTMENT VALUES (40, 'OPERATIONS', 'BOSTON');
//		 
//		INSERT INTO EMPLOYEE VALUES (7839, 'KING',   'PRESIDENT', NULL, '1981-11-17', 5000, NULL, 10);
//		    INSERT INTO EMPLOYEE VALUES (7566, 'JONES',  'MANAGER',   7839, '1981-04-02',  2975, NULL, 20);
//		       INSERT INTO EMPLOYEE VALUES(7788, 'SCOTT',  'ANALYST',   7566, '1982-12-09', 3000, NULL, 20);
//		          INSERT INTO EMPLOYEE VALUES(7876, 'ADAMS',  'CLERK',     7788, '1983-01-12', 1100, NULL, 20);
		          
	}
	
	
	
	private void executePStatement(Connection conn, String sql) throws SQLException {
		PreparedStatement pstmt = conn.prepareStatement(sql);
		try {
			pstmt.execute();
		} finally {
			pstmt.close();
		}
	}

	
	private int executePStatement_int(Connection conn, String sql) throws SQLException {
		PreparedStatement pstmt = conn.prepareStatement(sql);
		try {
			ResultSet rs = pstmt.executeQuery();
			rs.next();
			int res = rs.getInt(1);
			rs.close();
			return res;
		} finally {
			pstmt.close();
		}
	}
	
}
