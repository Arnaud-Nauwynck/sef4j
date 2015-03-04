package org.sef4j.jdbc.wrappers;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.ConnectionEventListener;
import javax.sql.StatementEventListener;
import javax.sql.XAConnection;
import javax.transaction.xa.XAResource;

import org.sef4j.callstack.CallStackElt.StackPopper;
import org.sef4j.callstack.LocalCallStack;

/**
 * Proxy for java.sql.XAConnection + wrapp all calls with push()/pop() + set params 
 * 
 */
public class SefXAConnectionProxy extends SefConnectionProxy implements XAConnection {

    /** redundant with <code>(XAConnection)super.to</code> */
    private XAConnection toXAConnection;
    
    protected SefConnectionProxy ownerSefConnectionProxy;
    protected SefXAResourceProxy cachedWrapperXAResource;
    protected XAResource cachedTargetXAResource;
    
    protected boolean logXAConnection = true;
    
	// ------------------------------------------------------------------------
	
	public SefXAConnectionProxy(SefConnectionProxy ownerSefConnectionProxy, XAConnection to, int connectionId) {
		super(null, connectionId); // TODO ... owner
		this.toXAConnection = to;
		this.ownerSefConnectionProxy = ownerSefConnectionProxy;
	}
	
	// ------------------------------------------------------------------------
	
	public Connection getConnection() throws SQLException {
		// ** do not delegate to   toXAConnection.getConnection() ! => return wrapper connection
		return ownerSefConnectionProxy;
	}

	public XAResource getXAResource() throws SQLException {
		XAResource res;
		XAResource xaResource = toXAConnection.getXAResource();
		if (xaResource == null) return null; // should not occur
		if (cachedTargetXAResource == xaResource && cachedWrapperXAResource != null) {
			res = cachedWrapperXAResource; // reuse same wrapper for same target return
		} else {
		    cachedTargetXAResource = xaResource;
			cachedWrapperXAResource = new SefXAResourceProxy(this, xaResource); 
			res = cachedWrapperXAResource;
		}
		return res;
	}
	
	// ------------------------------------------------------------------------
	
	public void addConnectionEventListener(ConnectionEventListener listener) {
	    StackPopper toPop = LocalCallStack.meth("addConnectionEventListener").push();
	    try {
			toXAConnection.addConnectionEventListener(listener);
		} catch(RuntimeException ex) {
		    throw LocalCallStack.pushPopParentException(ex);
		} finally {
		    toPop.close();
		}
	}

	public void removeConnectionEventListener(ConnectionEventListener listener) {
	    StackPopper toPop = LocalCallStack.meth("removeConnectionEventListener").push();
	    try {
	        toXAConnection.removeConnectionEventListener(listener);
	    } catch(RuntimeException ex) {
	        throw LocalCallStack.pushPopParentException(ex);
	    } finally {
	        toPop.close();
	    }
	}

	public void addStatementEventListener(StatementEventListener listener) {
	    StackPopper toPop = LocalCallStack.meth("addStatementEventListener").push();
        try {
            toXAConnection.addStatementEventListener(listener);
        } catch(RuntimeException ex) {
            throw LocalCallStack.pushPopParentException(ex);
        } finally {
            toPop.close();
        }
	}

	public void removeStatementEventListener(StatementEventListener listener) {
        StackPopper toPop = LocalCallStack.meth("removeStatementEventListener").push();
        try {
            toXAConnection.removeStatementEventListener(listener);
        } catch(RuntimeException ex) {
            throw LocalCallStack.pushPopParentException(ex);
        } finally {
            toPop.close();
        }
	}

	// ------------------------------------------------------------------------
	
	@Override
	public String toString() {
		return "SefXAConnectionProxy[to=" + toXAConnection + "]";
	}
	
}
