package org.sef4j.core.util.factorydef;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sef4j.core.util.Handle;
import org.sef4j.core.util.HandleGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * repository of object created by definition
 * 
 * <p>
 * This class handles creation of object by delegating to corresponding factory,<br/>
 * and ensure uniqueness of created objects with key def.<br/>
 * It manages reference counter per object, returning handles to safely register/unregister objects
 * </p>
 */
public class ObjectByDefRepository<TDef, TFactory extends ObjectByDefFactory<TDef,T>, T> {

	private static final Logger LOG = LoggerFactory.getLogger(ObjectByDefRepository.class);
	
	public static class ObjectWithHandle<T> implements Closeable {
		private ObjectByDefRepository<?,?,T> repository;
		private Handle handle;
		private T object;
		
		public ObjectWithHandle(ObjectByDefRepository<?,?,T> repository, Handle handle, T object) {
			this.handle = handle;
			this.object = object;
		}

		@Override
		public void close() throws IOException {
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

	private static class Entry<T> {
		T object;
		Set<Handle> handles = new HashSet<Handle>();
		Set<Handle> internalDependencyHandles = new HashSet<Handle>();
	}
	
	// ------------------------------------------------------------------------
	
	private final String displayObjectName;
	
	private HandleGenerator handleGenerator = new HandleGenerator();
	
	private Map<TDef,Entry<T>> entries = new HashMap<TDef,Entry<T>>();

	private Object entriesLock = new Object();
	
	// use copy-on-write?
	private List<TFactory> registeredFactories = new ArrayList<TFactory>();
	
	// ------------------------------------------------------------------------

	public ObjectByDefRepository(String displayObjectName) {
		this.displayObjectName = displayObjectName;
	}

	// ------------------------------------------------------------------------

	/**
	 * register an T by its key definition
	 * internally create on first use only and increment reference counter
	 * 
	 * @param def
	 * @return
	 */
	public ObjectWithHandle<T> register(TDef def) {
		Entry<T> res;
		Handle handle;
		synchronized(entriesLock) {
			res = entries.get(def);
			if (res == null) {
				// create T
				T obj = doCreateObjByDef(def);
				
				res = new Entry<T>();
				res.object = obj;
				entries.put(def, res);
			}
			handle = handleGenerator.generate();
			res.handles.add(handle);
		}
		return new ObjectWithHandle<T>(this, handle, res.object);
	}

	protected T doCreateObjByDef(TDef def) {
		ObjectByDefFactory<TDef,T> factory = findMatchingFactoryFor(def);
		if (factory == null) {
			throw new UnsupportedOperationException("Factory not found for def: " + def);
		}
		@SuppressWarnings("unchecked")
		ObjectByDefRepository<TDef,ObjectByDefFactory<TDef,T>,T> thisRepo = (ObjectByDefRepository<TDef,ObjectByDefFactory<TDef,T>,T>) this;
		
		// *** the biggy ***
		T obj = factory.create(def, thisRepo);
		
		return obj;
	}

	/**
	 * unregister an T by its created handle
	 * @param handle
	 */
	public void unregister(Handle handle) { 
		// note: could also double index by handle..
		synchronized(entriesLock) {
			Entry<T> entry = null;
			for(Iterator<Entry<T>> iter = entries.values().iterator(); iter.hasNext(); ) {
				Entry<T> e = iter.next();
				if (e.handles.remove(handle)) {
					entry = e;
					if (entry.handles.isEmpty()) {
						// remove this entry from map + recursive unregister dependencies + close T
						iter.remove(); 
						doUnregister(entry);
					}
					break;
				}
			}
			if (entry == null) {
				LOG.warn("unregister " + handle + " => not found! .. ignore, do nothing");
			}
		}		
	}

	private void doUnregister(Entry<T> entry) {
		// recursively unregister dependencies
		if (entry.internalDependencyHandles != null && !entry.internalDependencyHandles.isEmpty()) {
			for(Handle depHandle : entry.internalDependencyHandles) {
				unregister(depHandle);
			}
		}
		T obj = entry.object;
		entry.object = null;
		if (obj instanceof Closeable) {
			try {
				((Closeable) obj).close();
			} catch(Exception ex) {
				LOG.warn("Faied to close properly " + entry.object + " ... ignore, no rethrow!");
			}
		}
	}
	
	
	public TFactory findMatchingFactoryFor(TDef def) {
		TFactory res = null;
		for(TFactory f : registeredFactories) {
			if (f.accepts(def)) {
				res = f;
				break;
			}
		}
		return res;
	}

	// ------------------------------------------------------------------------

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(displayObjectName + "Repository[");
		synchronized(entriesLock) {
			sb.append(entries.size() + "entry(ies)");
		}
		sb.append(", " + registeredFactories.size() + " registeredFactories");
		sb.append("]");
		return sb.toString();
	}
	
}
