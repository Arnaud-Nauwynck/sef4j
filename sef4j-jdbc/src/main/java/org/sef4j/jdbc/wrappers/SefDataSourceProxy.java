package org.sef4j.jdbc.wrappers;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

/**
 *
 */
public class SefDataSourceProxy implements DataSource {

	protected DataSource to;
	
	// ------------------------------------------------------------------------

	public SefDataSourceProxy(DataSource to) {
		this.to = to;
	}

	// ------------------------------------------------------------------------
	
	public DataSource getUnderlying() {
		return to;
	}

	// implements java.sql.DataSource 
	// ------------------------------------------------------------------------

	public Connection getConnection() throws SQLException {
	    Connection toConn = to.getConnection();
		return new SefConnectionProxy(toConn);
	}

	public Connection getConnection(String username, String password) throws SQLException {
	    Connection toConn = to.getConnection(username, password);
        return new SefConnectionProxy(toConn);
	}

	public PrintWriter getLogWriter() throws SQLException {
		return to.getLogWriter();
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		return to.unwrap(iface);
	}

	public void setLogWriter(PrintWriter out) throws SQLException {
		to.setLogWriter(out);
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return to.isWrapperFor(iface);
	}

	public void setLoginTimeout(int seconds) throws SQLException {
		to.setLoginTimeout(seconds);
	}

	public int getLoginTimeout() throws SQLException {
		return to.getLoginTimeout();
	}
	
}
