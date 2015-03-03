package org.sef4j.jdbc.wrappers;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.sef4j.callstack.CallStackElt.StackPopper;
import org.sef4j.callstack.LocalCallStack;

/**
 * java.sql.DataSource proxy instrumented for using LocalCallStack.push()/pop()
 */
public class SefDataSourceProxy extends SefCommonDataSourceProxy implements DataSource {

	protected DataSource to;
	
	// ------------------------------------------------------------------------

	public SefDataSourceProxy(DataSource to) {
	    super(to);
		this.to = to;
	}

	// ------------------------------------------------------------------------
	
	public DataSource getUnderlying() {
		return to;
	}

	// implements java.sql.DataSource 
	// ------------------------------------------------------------------------

	public Connection getConnection() throws SQLException {
	    StackPopper toPop = LocalCallStack.meth("getConnection").push();
	    try {
    	    
	        Connection toConn = to.getConnection();
    		SefConnectionProxy res = new SefConnectionProxy(toConn);
    		
    		return LocalCallStack.pushPopParentReturn(res);
        } catch(SQLException ex) {
            throw LocalCallStack.pushPopParentException(ex);
        } catch(RuntimeException ex) {
            throw LocalCallStack.pushPopParentException(ex);
	    } finally {
	        toPop.close();
	    }
	}

	public Connection getConnection(String username, String password) throws SQLException {
        StackPopper toPop = LocalCallStack.meth("getConnection_user").push();
        try {
            
            Connection toConn = to.getConnection(username, password);
            SefConnectionProxy res = new SefConnectionProxy(toConn);
            
            return LocalCallStack.pushPopParentReturn(res);
        } catch(SQLException ex) {
            throw LocalCallStack.pushPopParentException(ex);
        } catch(RuntimeException ex) {
            throw LocalCallStack.pushPopParentException(ex);
        } finally {
            toPop.close();
        }
	}

}
