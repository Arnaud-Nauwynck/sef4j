package org.sef4j.core.util.factorydef;

import java.io.Closeable;

import org.sef4j.core.util.Handle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SharedRef<T> implements Closeable {
	
	private static final Logger LOG = LoggerFactory.getLogger(SharedRef.class);
	
	private SharedObjectByDefRepository<?,?> repository;
	private Handle handle;
	private T object;

	// ------------------------------------------------------------------------
	
	public SharedRef(SharedObjectByDefRepository<?,?> repository, Handle handle, T object) {
		this.handle = handle;
		this.object = object;
	}

	// ------------------------------------------------------------------------
	
	@Override
	protected void finalize() {
		if (handle != null) {
			LOG.warn("detected missing close() for shared object ref while garbage collecting ... close it!");
			close();
		}
	}


	@Override
	public void close() {
		Handle tmpHandle = handle;
		if (tmpHandle != null) {
			SharedObjectByDefRepository<?,?> tmpRepository = repository;
			handle = null;
			repository = null;
			object = null;

			try {
				tmpRepository.unregister(tmpHandle);
			} catch(Exception ex) {
				LOG.warn("Failed to unregister shared ref handle: " + ex.getMessage() + " ...ignore, no retrhow!");
				// no rethrow
			}
		}
	}

	public T getObject() {
		return object;
	}

	// ------------------------------------------------------------------------
	
	@Override
	public String toString() {
		return "SharedRef[" + object + "]";
	}

}