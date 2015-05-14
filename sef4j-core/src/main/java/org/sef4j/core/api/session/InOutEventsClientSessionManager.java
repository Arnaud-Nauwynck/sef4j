package org.sef4j.core.api.session;

import org.sef4j.core.util.CopyOnWriteUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

/**
 * 
 * @param <K>
 */
public class InOutEventsClientSessionManager<K> {

	private static final Logger LOG = LoggerFactory.getLogger(InOutEventsClientSessionManager.class);
	
	/** copy-on-write map of ClientSessions */
	private ImmutableMap<K,InOutEventsClientSession> clientSessions = ImmutableMap.of();
	
	private Object clientSessionsWriteLock = new Object();
	
	
	// ------------------------------------------------------------------------

	public InOutEventsClientSessionManager() {
	}

	// ------------------------------------------------------------------------

	public InOutEventsClientSession getClientSessionOrNull(K id) {
		return clientSessions.get(id);
	}

	public InOutEventsClientSession getClientSessionOrCreate(K id) {
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

	public InOutEventsClientSession createClientSession(K id) {
		LOG.info("createClientSession " + id);
		InOutEventsClientSession res;
		synchronized (clientSessionsWriteLock) {
			if (clientSessions.containsKey(id)) throw new IllegalArgumentException();
			res = new InOutEventsClientSession(id.toString());
			this.clientSessions = CopyOnWriteUtils.newWithPut(clientSessions, id, res);
		}
		return res;
	}

	public void deleteClientSession(K id) {
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

	// ------------------------------------------------------------------------

	@Override
	public String toString() {
		return "InOutEventsClientSessionManager [" 
				+ clientSessions.size() + " clientSession(s)" 
				+ "]";
	}

}
