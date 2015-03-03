package org.sef4j.jdbc.wrappers;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import java.util.Properties;

/**
 * Proxy for java.sql.XAConnection + wrapp all calls with push()/post() + set params 
 * 
 */
public class SefXAResourceProxy implements XAResource {

	/** underlying for proxy */
    private final XAResource to;

    // ------------------------------------------------------------------------

	public SefXAResourceProxy(XAResource to, int connectionId) {
		// super(owner, connectionId);
		this.to = to;
	}

	// ------------------------------------------------------------------------

	public void start(Xid xid, int flags) throws XAException {
		boolean log = isCurrLogXAConnection();
		if (log) {
			callInfoLogger.pre("start", "" + xid + " " + flags);
		}
		try {
			to.start(xid, flags);
			if (log) {
				callInfoLogger.postIgnoreVoid();
			}
		} catch(XAException ex) {
			if (log) {
				callInfoLogger.postEx(ex);
			}
			throw ex;
		} catch(RuntimeException ex) {
			if (log) {
				callInfoLogger.postEx(ex);
			}
			throw ex;
		}
	}
	

	public int prepare(Xid xid) throws XAException {
		int res;
		boolean log = isCurrLogXAConnection();
		if (log) {
			callInfoLogger.pre("prepare", "" + xid);
		}
		try {
			res = to.prepare(xid);
			if (log) {
				callInfoLogger.postDefaultRes(res);
			}
		} catch(XAException ex) {
			if (log) {
				callInfoLogger.postEx(ex);
			}
			throw ex;
		} catch(RuntimeException ex) {
			if (log) {
				callInfoLogger.postEx(ex);
			}
			throw ex;
		}
		return res;
	}

	
	public void commit(Xid arg0, boolean arg1) throws XAException {
		boolean log = isCurrLogXAConnection();
		if (log) {
			callInfoLogger.pre("commit", "" + arg0 + " " + arg1);
		}
		try {
			to.commit(arg0, arg1);
			if (log) {
				callInfoLogger.postIgnoreVoid();
			}
		} catch(XAException ex) {
			if (log) {
				callInfoLogger.postEx(ex);
			}
			throw ex;
		} catch(RuntimeException ex) {
			if (log) {
				callInfoLogger.postEx(ex);
			}
			throw ex;
		}
	}
	

	public void rollback(Xid arg0) throws XAException {
		boolean log = isCurrLogXAConnection();
		if (log) {
			callInfoLogger.pre("rollback", "" + arg0);
		}
		try {
			to.rollback(arg0);
			if (log) {
				callInfoLogger.postIgnoreVoid();
			}
		} catch(XAException ex) {
			if (log) {
				callInfoLogger.postEx(ex);
			}
			throw ex;
		} catch(RuntimeException ex) {
			if (log) {
				callInfoLogger.postEx(ex);
			}
			throw ex;
		}
	}
	

	public void end(Xid arg0, int arg1) throws XAException {
		boolean log = isCurrLogXAConnection();
		if (log) {
			callInfoLogger.pre("end", "" + arg0 + " " + arg1);
		}
		try {
			to.end(arg0, arg1);
			if (log) {
				callInfoLogger.postIgnoreVoid();
			}
		} catch(XAException ex) {
			if (log) {
				callInfoLogger.postEx(ex);
			}
			throw ex;
		} catch(RuntimeException ex) {
			if (log) {
				callInfoLogger.postEx(ex);
			}
			throw ex;
		}
	}

	public void forget(Xid xid) throws XAException {
		boolean log = isCurrLogXAConnection();
		if (log) {
			callInfoLogger.pre("forget", "" + xid);
		}
		try {
			to.forget(xid);
			if (log) {
				callInfoLogger.postIgnoreVoid();
			}
		} catch(XAException ex) {
			if (log) {
				callInfoLogger.postEx(ex);
			}
			throw ex;
		} catch(RuntimeException ex) {
			if (log) {
				callInfoLogger.postEx(ex);
			}
			throw ex;
		}
	}


	public Xid[] recover(int xid) throws XAException {
		Xid[] res;
		boolean log = isCurrLogXAConnection();
		if (log) {
			callInfoLogger.pre("recover", "" + xid);
		}
		try {
			res = to.recover(xid);
			if (log) {
				callInfoLogger.postRes(res);
			}
		} catch(XAException ex) {
			if (log) {
				callInfoLogger.postEx(ex);
			}
			throw ex;
		} catch(RuntimeException ex) {
			if (log) {
				callInfoLogger.postEx(ex);
			}
			throw ex;
		}
		return res;
	}

	
	public boolean isSameRM(XAResource arg0) throws XAException {
		return to.isSameRM(arg0);
	}

	public int getTransactionTimeout() throws XAException {
		return to.getTransactionTimeout();
	}

	public boolean setTransactionTimeout(int arg0) throws XAException {
		boolean res;
		boolean log = isCurrLogXAConnection();
		if (log) {
			callInfoLogger.pre("setTransactionTimeout", "" + arg0);
		}
		try {
			res = to.setTransactionTimeout(arg0);
			if (log) {
				callInfoLogger.postIgnoreRes(res);
			}
		} catch(XAException ex) {
			if (log) {
				callInfoLogger.postEx(ex);
			}
			throw ex;
		} catch(RuntimeException ex) {
			if (log) {
				callInfoLogger.postEx(ex);
			}
			throw ex;
		}
		return res;
	}
	
}
