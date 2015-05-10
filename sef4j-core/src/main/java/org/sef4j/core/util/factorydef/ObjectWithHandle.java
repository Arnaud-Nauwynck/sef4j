package org.sef4j.core.util.factorydef;

import java.io.Closeable;

import org.sef4j.core.util.Handle;

public class ObjectWithHandle<T> implements Closeable {
	private ObjectByDefRepository<?,?> repository;
	private Handle handle;
	private T object;
	
	public ObjectWithHandle(ObjectByDefRepository<?,?> repository, Handle handle, T object) {
		this.handle = handle;
		this.object = object;
	}

	@Override
	public void close() {
		if (handle != null) {
			repository.unregister(handle);
			handle = null;
			repository = null;
			object = null;
		}
	}

	public T getObject() {
		return object;
	}
	
}