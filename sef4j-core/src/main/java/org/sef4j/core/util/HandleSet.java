package org.sef4j.core.util;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import org.sef4j.core.util.IStartableSupport.StartStopMethods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * basic helper for Handle Generator + Set<>
 * this class can optionnally delegate to IStartableSupport start() on first handle creation, and stop() on last handle deletion
 */
public class HandleSet {

	private static final Logger LOG = LoggerFactory.getLogger(HandleSet.class);
	
	private HandleGenerator handleGenerator = new HandleGenerator();
	
	private Set<Handle> handles = new HashSet<Handle>();

	private IStartableSupport startableCallback;
	
	// ------------------------------------------------------------------------

	public HandleSet(IStartableSupport startableCallback) {
		this.startableCallback = startableCallback;
	}

	public HandleSet(Supplier<Boolean> isStarted, Runnable startCallback, Runnable stopCallback) {
		this(new StartStopMethods(isStarted, startCallback, stopCallback));
	}

	// ------------------------------------------------------------------------

	public Handle addGenerated() {
		Handle res = handleGenerator.generate();
		boolean needStart;
		synchronized(handles) {
			needStart = handles.isEmpty();
			handles.add(res);
		}
		if (needStart) {
			doStart();
		}
		return res;
	}
	
	/** @return remaining handle count */
	public void remove(Handle handle) {
		boolean needStop;
		synchronized(handles) {
			boolean removed = handles.remove(handle);
			needStop = removed && handles.isEmpty();
		}
		if (needStop) {
			doStop();
		}
	}

	public void clear() {
		boolean needStop;
		synchronized(handles) {
			needStop = ! handles.isEmpty();
			handles.clear();
		}
		if (needStop) {
			doStop();
		}
	}

	protected void doStart() {
		if (startableCallback != null) {
			try {
				startableCallback.start();
			} catch(Exception ex) {
				// should not occur... if start() is unsafe => should manage retryal by underlying object + use async thread
				// => remove handle + rethrow?! .. or catch exception
				LOG.error("Failed to start() on first handle acqquired: " + startableCallback + " ... ignore, no rethrow?!", ex);
			}
		}
	}

	protected void doStop() {
		if (startableCallback != null) {
			try {
				startableCallback.stop();
			} catch(Exception ex) {
				// should not occur... if start() is unsafe => should manage retryal by underlying object + use async thread
				// => remove handle + rethrow?! .. or catch exception
				LOG.error("Failed to stop() on last handle release: " + startableCallback + " ... ignore, no rethrow?!", ex);
			}
		}
	}
	
}
