package org.sef4j.core.util.factorydef;

import java.io.Closeable;

import org.sef4j.core.util.Handle;
import org.sef4j.core.util.HandleSet;
import org.sef4j.core.util.ISharedStartableSupport;
import org.sef4j.core.util.IStartableSupport;

/**
 * helper abstract class for Object with associated definition, supporting ISharedStartableSupport via handles
 *
 * @param <TDef>
 */
public abstract class AbstractStartableDefObject<TDef> implements Closeable, ISharedStartableSupport, IStartableSupport {
	
	private final TDef def;

	private final String displayName;
	
	private HandleSet autoStartableHandleSet = new HandleSet(this);
	
	// ------------------------------------------------------------------------
	
	protected AbstractStartableDefObject(TDef def, String displayName) {
		this.def = def;
		this.displayName = displayName;
	}

	// ------------------------------------------------------------------------

	public TDef getDef() {
		return def;
	}

	// ------------------------------------------------------------------------

	// cf Closeable
	@Override
	public void close() {
		autoStartableHandleSet.clear();
		this.autoStartableHandleSet = null;
		// this.def = null;
		if (isStarted()) {
			stop();
		}
	}

	// cf ISharedStartableSupport
	
	@Override
	public Handle registerStart() {
		return autoStartableHandleSet.addGenerated();
	}
	
	@Override
	public void unregisterStop(Handle handle) {
		autoStartableHandleSet.remove(handle);
	}
	
	// cf IStartableSupport
	@Override
	public abstract boolean isStarted();
	@Override
	public abstract void start();
	@Override
	public abstract void stop();

	// ------------------------------------------------------------------------
	
	@Override
	public String toString() {
		return "InputEventChain [" + displayName + "]";
	}
	
}
