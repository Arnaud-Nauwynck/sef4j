package org.sef4j.jdbc.wrappers;

import java.sql.SQLException;

import javax.sql.XAConnection;
import javax.transaction.xa.XAResource;

import org.sef4j.callstack.LocalCallStack;
import org.sef4j.callstack.CallStackElt.StackPopper;

/**
 * Proxy for java.sql.XAConnection + wrapp all calls with push()/pop() + return wrapped object
 * 
 */
public class SefXAConnectionProxy extends SefPooledConnectionProxy implements XAConnection {

    /** redundant with <code>(XAConnection)super.to</code> */
    private XAConnection to;
    
    protected SefXAResourceProxy cachedWrapperXAResource;
    protected XAResource cachedTargetXAResource;
    
	// ------------------------------------------------------------------------
	
	public SefXAConnectionProxy(SefConnectionProxy ownerSefConnectionProxy, XAConnection to) {
		super(to); // TOADD ... owner
		this.to = to;
	}
	
	// ------------------------------------------------------------------------
	
	public XAResource getXAResource() throws SQLException {
        StackPopper toPop = LocalCallStack.meth("getXAResource").push();
        try {
    		XAResource tmpres = to.getXAResource();  		
    		SefXAResourceProxy res = wrapOrReuseWrapper(tmpres);
    		return LocalCallStack.pushPopParentReturn(res);
        } catch(SQLException ex) {
            throw LocalCallStack.pushPopParentException(ex);
        } finally {
            toPop.close();
        }
	}

	private SefXAResourceProxy wrapOrReuseWrapper(XAResource tmpres) {
		// wrap result or reuse last wrapper... TODO may use Map<XAResource,SefXAResourceProxy>
		if (tmpres == null) return null; // should not occur
		SefXAResourceProxy res;
		if (cachedTargetXAResource == tmpres && cachedWrapperXAResource != null) {
			res = cachedWrapperXAResource; // reuse same wrapper for same target return
		} else {
		    cachedTargetXAResource = tmpres;
			cachedWrapperXAResource = new SefXAResourceProxy(this, tmpres); 
			res = cachedWrapperXAResource;
		}
		return res;
	}

	// ------------------------------------------------------------------------
	
	@Override
	public String toString() {
		return "SefXAConnectionProxy[to=" + to + "]";
	}
	
}
