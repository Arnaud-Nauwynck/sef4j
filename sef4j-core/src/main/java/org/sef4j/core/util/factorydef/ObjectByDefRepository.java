package org.sef4j.core.util.factorydef;

import java.io.Closeable;
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
public class ObjectByDefRepository<TDef,T> {

	private static final Logger LOG = LoggerFactory.getLogger(ObjectByDefRepository.class);
	
	private static class Entry<T> {
		T object;
		Set<Handle> handles = new HashSet<Handle>();
		Set<Handle> internalDependencyHandles = new HashSet<Handle>();
	}
	
	// ------------------------------------------------------------------------
	
	private final String displayObjectName;
	
	private HandleGenerator handleGenerator = new HandleGenerator();
	
	private ObjectByDefRepositories owner;
	
	private Map<TDef,Entry<T>> entries = new HashMap<TDef,Entry<T>>();

	private Object entriesLock = new Object();
	
	// use copy-on-write?
	private List<ObjectByDefFactory<TDef,T>> registeredFactories = new ArrayList<ObjectByDefFactory<TDef,T>>();
	
	// ------------------------------------------------------------------------

	public ObjectByDefRepository(ObjectByDefRepositories owner, String displayObjectName) {
		this.owner = owner;
		this.displayObjectName = displayObjectName;
	}

	// ------------------------------------------------------------------------

	/**
	 * register an object by its key definition
	 * internally create on first use and increment reference counter
	 * 
	 * @param def
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <X extends T> ObjectWithHandle<X> getOrCreateByDef(TDef def) {
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
		return new ObjectWithHandle<X>(this, handle, (X) res.object);
	}

	protected T doCreateObjByDef(TDef def) {
		ObjectByDefFactory<TDef,T> factory = findMatchingFactoryFor(def);
		if (factory == null) {
			throw new UnsupportedOperationException("Factory not found for def: " + def);
		}
		// *** the biggy ***
		T obj = factory.create(def, owner);
		
		return obj;
	}

	/**
	 * unregister an object by its created handle
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
	
	
	public ObjectByDefFactory<TDef,T> findMatchingFactoryFor(TDef def) {
		ObjectByDefFactory<TDef,T> res = null;
		for(ObjectByDefFactory<TDef,T> f : registeredFactories) {
			if (f.accepts(def)) {
				res = f;
				break;
			}
		}
		return res;
	}

	public void registerFactory(ObjectByDefFactory<TDef,T> factory) {
		registeredFactories.add(factory);
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
