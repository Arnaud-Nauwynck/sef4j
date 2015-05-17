package org.sef4j.core.api.session;

import org.sef4j.core.util.CopyOnWriteUtils;
import org.sef4j.core.util.factorydef.ObjectByDefRepositories;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

/**
 * 
 * @param <K>
 */
public class InOutEventsClientSessionManager {

	private static final Logger LOG = LoggerFactory.getLogger(InOutEventsClientSessionManager.class);
	
	/** copy-on-write map of ClientSessions */
	private ImmutableMap<String,InOutEventsClientSession> clientSessions = ImmutableMap.of();
	
	private Object clientSessionsWriteLock = new Object();
	
	private ObjectByDefRepositories sharedObjByDefRepositories;
	
	// ------------------------------------------------------------------------

	public InOutEventsClientSessionManager(ObjectByDefRepositories sharedObjByDefRepositories) {
		this.sharedObjByDefRepositories = sharedObjByDefRepositories;
	}

	// ------------------------------------------------------------------------

	public InOutEventsClientSession getClientSessionOrNull(String id) {
		return clientSessions.get(id);
	}

	public InOutEventsClientSession getClientSessionOrCreate(String id) {
		InOutEventsClientSession res = clientSessions.get(id);
		if (res == null) {
			synchronized (clientSessionsWriteLock) {
				res = clientSessions.get(id); // redo within lock
				if (res == null) {
					res = createClientSession(id);
				}
			}			
		}
		return res;
	}

	public InOutEventsClientSession createClientSession(String id) {
		LOG.info("createClientSession " + id);
		InOutEventsClientSession res;
		synchronized (clientSessionsWriteLock) {
			if (clientSessions.containsKey(id)) throw new IllegalArgumentException();
			String displayName = id; // to change?
			res = new InOutEventsClientSession(this, id, displayName);
			this.clientSessions = CopyOnWriteUtils.newWithPut(clientSessions, id, res);
		}
		return res;
	}

	public void deleteClientSession(String id) {
		LOG.info("deleteClientSession " + id);
		InOutEventsClientSession res;
		synchronized (clientSessionsWriteLock) {
			res = clientSessions.get(id);
			this.clientSessions = CopyOnWriteUtils.newWithRemove(clientSessions, id);
		}
		try {
			res.close();
		} catch(Exception ex) {
			LOG.warn("Failed to dispose ClientSession ... ignore, no rethrow!", ex);
		}
	}

	/*pp*/ ObjectByDefRepositories getSharedObjByDefRepositories() {
		return sharedObjByDefRepositories;
	}

	// ------------------------------------------------------------------------

	@Override
	public String toString() {
		return "InOutEventsClientSessionManager [" 
				+ clientSessions.size() + " clientSession(s)" 
				+ "]";
	}

}
