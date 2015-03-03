package org.sef4j.jdbc.wrappers;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.ConnectionEventListener;
import javax.sql.StatementEventListener;
import javax.sql.XAConnection;
import javax.transaction.xa.XAResource;

/**
 * Proxy for java.sql.XAConnection + wrapp all calls with push()/pop() + set params 
 * 
 */
public class SefXAConnectionProxy extends SefConnectionProxy implements XAConnection {

    /** redundant with <code>(XAConnection)super.to</code> */
    private XAConnection toXAConnection;
    
    protected SefXAResourceProxy cachedWrapperXAResource;
    protected XAResource cachedTargetXAResource;
    
    protected boolean logXAConnection = true;
    
	// ------------------------------------------------------------------------
	
	public SefXAConnectionProxy(ConnectionFactoryConfig owner, XAConnection to, int connectionId) {
		super(owner, to, connectionId);
		this.toXAConnection = to;
	}
	
	// ------------------------------------------------------------------------
	
	/** internal accesser / downcast */
	public XAConnection getUnderlyingXAConnection() {
        return (XAConnection) getUnderlyingConnection();
    }

	
	public Connection getConnection() throws SQLException {
		// ** do not delegate to getUnderlyingXAConnection().getConnection() ! => return wrapper connection
		return getUnderlyingConnection();
	}

	public XAResource getXAResource() throws SQLException {
		XAResource res;
		XAResource xaResource = getUnderlyingXAConnection().getXAResource();
		if (xaResource == null) return null; // should not occur
		if (cachedTargetXAResource == xaResource && cachedWrapperXAResource != null) {
			// ** optim: use cache fro wrapper **
			res = cachedWrapperXAResource;
		} else {
			res = cachedWrapperXAResource = new SefXAResourceProxy(owner, logger, xaResource, spid, loggerProperties);
			cachedTargetXAResource = xaResource;
		}
		return res;
	}
	
	// ------------------------------------------------------------------------
	
	public void addConnectionEventListener(ConnectionEventListener listener) {
		boolean log = isCurrLogXAConnection();
		if (log) {
			callInfoLogger.pre("addConnectionEventListener", "" + listener);
		}
		try {
			getUnderlyingXAConnection().addConnectionEventListener(listener);
			if (log) {
				callInfoLogger.postIgnoreVoid();
			}
		} catch(RuntimeException ex) {
			if (log) {
				callInfoLogger.postEx(ex);
			}
			throw ex;
		}
	}

	public void removeConnectionEventListener(ConnectionEventListener listener) {
		boolean log = isCurrLogXAConnection();
		if (log) {
			callInfoLogger.pre("removeConnectionEventListener", "" + listener);
		}
		try {
			getUnderlyingXAConnection().removeConnectionEventListener(listener);
			if (log) {
				callInfoLogger.postIgnoreVoid();
			}
		} catch(RuntimeException ex) {
			if (log) {
				callInfoLogger.postEx(ex);
			}
			throw ex;
		}
	}

	public void addStatementEventListener(StatementEventListener listener) {
		boolean log = isCurrLogXAConnection();
		if (log) {
			callInfoLogger.pre("addStatementEventListener", "" + listener);
		}
		try {
			getUnderlyingXAConnection().addStatementEventListener(listener);
			if (log) {
				callInfoLogger.postIgnoreVoid();
			}
		} catch(RuntimeException ex) {
			if (log) {
				callInfoLogger.postEx(ex);
			}
			throw ex;
		}
	}

	public void removeStatementEventListener(StatementEventListener listener) {
		boolean log = isCurrLogXAConnection();
		if (log) {
			callInfoLogger.pre("removeStatementEventListener", "" + listener);
		}
		try {
			getUnderlyingXAConnection().removeStatementEventListener(listener);
			if (log) {
				callInfoLogger.postIgnoreVoid();
			}
		} catch(RuntimeException ex) {
			if (log) {
				callInfoLogger.postEx(ex);
			}
			throw ex;
		}
	}


	// ------------------------------------------------------------------------
	
	@Override
	public String toString() {
		return "XASefWrappedConnection[" + super.toString() + "]";
	}
	
}
