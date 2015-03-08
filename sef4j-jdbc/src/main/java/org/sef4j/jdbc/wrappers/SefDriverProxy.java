package org.sef4j.jdbc.wrappers;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

import org.sef4j.callstack.CallStackElt.StackPopper;
import org.sef4j.callstack.LocalCallStack;

/**
 * java.sql.Driver proxy instrumented for using LocalCallStack.push()/pop() + return wrapped Connection
 */
public class SefDriverProxy implements Driver {

	private static final String CNAME = SefDriverProxy.class.getName();
	
	private Driver to;

	// ------------------------------------------------------------------------
	
	public SefDriverProxy(Driver to) {
		this.to = to;
	}

	// ------------------------------------------------------------------------
	
	@Override
	public Connection connect(String url, Properties info) throws SQLException {
        StackPopper toPop = LocalCallStack.meth(CNAME, "connect")
        		.withParam("url", url)
        		.withParam("info", info) // NOTICE: may contains security credentials!...
        		.push();
        try {
        	Connection tmpres = to.connect(url, info);

            SefConnectionProxy res = new SefConnectionProxy(null, tmpres);
            return toPop.returnValue(res);
        } catch(SQLException ex) {
            throw toPop.returnException(ex);
        } finally {
            toPop.close();
        }
	}

	@Override
	public boolean acceptsURL(String url) throws SQLException {
        StackPopper toPop = LocalCallStack.meth(CNAME, "acceptsURL")
        		.withParam("url", url)
        		.push();
        try {
        	boolean res = to.acceptsURL(url);
            return toPop.returnValue(res);
        } catch(SQLException ex) {
            throw toPop.returnException(ex);
        } finally {
            toPop.close();
        }
	}

	@Override
	public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
		return to.getPropertyInfo(url, info);
	}

	@Override
	public int getMajorVersion() {
        StackPopper toPop = LocalCallStack.meth(CNAME, "getMajorVersion").push();
        try {
        	int res = to.getMajorVersion();
            return toPop.returnValue(res);
        } finally {
            toPop.close();
        }
	}

	@Override
	public int getMinorVersion() {
        StackPopper toPop = LocalCallStack.meth(CNAME, "getMinorVersion").push();
        try {
        	int res = to.getMinorVersion();
            return toPop.returnValue(res);
        } finally {
            toPop.close();
        }
	}

	@Override
	public boolean jdbcCompliant() {
        StackPopper toPop = LocalCallStack.meth(CNAME, "jdbcCompliant").push();
        try {
        	boolean res = to.jdbcCompliant();
            return toPop.returnValue(res);
        } finally {
            toPop.close();
        }
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        StackPopper toPop = LocalCallStack.meth(CNAME, "getParentLogger").push();
        try {
        	Logger res = to.getParentLogger();
            return toPop.returnValue(res);
        } catch(SQLFeatureNotSupportedException ex) {
        	throw toPop.returnException(ex);
        } finally {
            toPop.close();
        }
	}

	// ------------------------------------------------------------------------

	@Override
	public String toString() {
		return "SefDriverProxy [to=" + to + "]";
	}
		
}
