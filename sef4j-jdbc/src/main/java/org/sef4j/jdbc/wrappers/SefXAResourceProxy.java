package org.sef4j.jdbc.wrappers;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import org.sef4j.callstack.CallStackElt.StackPopper;
import org.sef4j.callstack.LocalCallStack;

/**
 * Proxy for java.sql.XAConnection + wrapp all calls with push()/post() + set params 
 * 
 */
public class SefXAResourceProxy implements XAResource {

	/** underlying for proxy */
    private final XAResource to;

    private SefXAConnectionProxy owner;
    
    // ------------------------------------------------------------------------

	public SefXAResourceProxy(SefXAConnectionProxy owner, XAResource to) {
		// super(owner, connectionId);
		this.to = to;
		this.owner = owner;
	}

	// ------------------------------------------------------------------------
	
	public XAResource getUnderlyingXAResource() {
		return to;
	}
	
	public SefXAConnectionProxy getOwner() {
		return owner;
	}
	
	// ------------------------------------------------------------------------

	public void start(Xid xid, int flags) throws XAException {
        StackPopper toPop = LocalCallStack.meth("start")
        		.withParam("xid", xid)
        		.withParam("flags", flags)
        		.push();
        try {
    		to.start(xid, flags);
        } catch(XAException ex) {
            throw toPop.returnException(ex);
        } finally {
            toPop.close();
        }
	}

	public int prepare(Xid xid) throws XAException {
        StackPopper toPop = LocalCallStack.meth("prepare")
        		.withParam("xid", xid)
        		.push();
        try {
    		int res = to.prepare(xid);
    		return toPop.returnValue(res);
        } catch(XAException ex) {
            throw toPop.returnException(ex);
        } finally {
            toPop.close();
        }
	}

	public void commit(Xid xid, boolean onePhase) throws XAException {
        StackPopper toPop = LocalCallStack.meth("commit")
        		.withParam("xid", xid)
        		.withParam("onePhase", onePhase)
        		.push();
        try {
    		to.commit(xid, onePhase);
        } catch(XAException ex) {
            throw toPop.returnException(ex);
        } finally {
            toPop.close();
        }
	}
	
	public void rollback(Xid xid) throws XAException {
        StackPopper toPop = LocalCallStack.meth("rollback")
        		.withParam("xid", xid)
        		.push();
        try {
    		to.rollback(xid);
        } catch(XAException ex) {
            throw toPop.returnException(ex);
        } finally {
            toPop.close();
        }
	}

	public void end(Xid xid, int flag) throws XAException {
        StackPopper toPop = LocalCallStack.meth("end")
        		.withParam("xid", xid)
        		.withParam("flag", flag)
        		.push();
        try {
    		to.end(xid, flag);
        } catch(XAException ex) {
            throw toPop.returnException(ex);
        } finally {
            toPop.close();
        }
	}

	public void forget(Xid xid) throws XAException {
        StackPopper toPop = LocalCallStack.meth("forget")
        		.withParam("xid", xid)
        		.push();
        try {
    		to.forget(xid);
        } catch(XAException ex) {
            throw toPop.returnException(ex);
        } finally {
            toPop.close();
        }
	}

	public Xid[] recover(int xid) throws XAException {
        StackPopper toPop = LocalCallStack.meth("recover")
        		.withParam("xid", xid)
        		.push();
        try {
    		Xid[] res = to.recover(xid);
            return toPop.returnValue(res);
        } catch(XAException ex) {
            throw toPop.returnException(ex);
        } finally {
            toPop.close();
        }
	}

	public boolean isSameRM(XAResource xares) throws XAException {
        StackPopper toPop = LocalCallStack.meth("isSameRM")
        		.withParam("xares", xares)
        		.push();
        try {
        	XAResource xaresUnwrapped = (xares instanceof SefXAResourceProxy)? 
        			((SefXAResourceProxy)xares).to : xares;
    		boolean res = to.isSameRM(xaresUnwrapped);
            return toPop.returnValue(res);
        } catch(XAException ex) {
            throw toPop.returnException(ex);
        } finally {
            toPop.close();
        }
	}

	public int getTransactionTimeout() throws XAException {
        StackPopper toPop = LocalCallStack.meth("getTransactionTimeout").push();
        try {
    		int res = to.getTransactionTimeout();
            return toPop.returnValue(res);
        } catch(XAException ex) {
            throw toPop.returnException(ex);
        } finally {
            toPop.close();
        }
	}

	public boolean setTransactionTimeout(int seconds) throws XAException {
        StackPopper toPop = LocalCallStack.meth("setTransactionTimeout")
        		.withParam("seconds", seconds).push();
        try {
    		boolean res = to.setTransactionTimeout(seconds);
            return toPop.returnValue(res);
        } catch(XAException ex) {
            throw toPop.returnException(ex);
        } finally {
            toPop.close();
        }
	}

	// ------------------------------------------------------------------------

	@Override
	public String toString() {
		return "SefXAResourceProxy [to=" + to + "]";
	}

}
