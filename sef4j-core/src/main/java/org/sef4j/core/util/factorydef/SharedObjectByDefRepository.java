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

import com.google.common.collect.ImmutableList;

/**
 * repository of object created by definition
 * 
 * <p>
 * This class handles creation of object by delegating to corresponding factory,<br/>
 * and ensure uniqueness of created objects with key def.<br/>
 * It manages reference counter per object, returning handles to safely register/unregister objects
 * </p>
 */
public class SharedObjectByDefRepository<TDef,T> {

	private static final Logger LOG = LoggerFactory.getLogger(SharedObjectByDefRepository.class);

	private final String displayObjectName;
	
	private HandleGenerator handleGenerator = new HandleGenerator();
	
	private ObjectByDefRepositories owner;
	
	private Map<DefKeyPair<TDef>,Entry<T>> entries = new HashMap<DefKeyPair<TDef>,Entry<T>>();

	private Object entriesLock = new Object();
	
	// use copy-on-write?
	private List<SharedObjectByDefFactory<TDef,T>> registeredFactories = new ArrayList<SharedObjectByDefFactory<TDef,T>>();
	
	// ------------------------------------------------------------------------

	public SharedObjectByDefRepository(ObjectByDefRepositories owner, String displayObjectName) {
		this.owner = owner;
		this.displayObjectName = displayObjectName;
	}

	public void close() {
		synchronized(entriesLock) {
			for(Entry<T> e : entries.values()) {
				doUnregister(e);
			}
			this.entries.clear();
		}
		// this.entries = null;
	}
	
	// ------------------------------------------------------------------------

	public <X extends T> SharedRef<X> getOrCreateByDef(TDef def) {
		return getOrCreateByDef(def, null);
	}
	
	/**
	 * register an object by its definition
	 * internally create on first use and increment reference counter
	 * 
	 * @param def
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <X extends T> SharedRef<X> getOrCreateByDef(TDef def, Object key) {
		DefKeyPair<TDef> defKey = new DefKeyPair<TDef>(def, key);
		Entry<T> entry;
		Handle handle;
		synchronized(entriesLock) {
			entry = entries.get(defKey);
			if (entry == null) {
				// create T
				entry = doCreateObjByDef(def);
				
				entries.put(defKey, entry);
			}
			handle = handleGenerator.generate();
			entry.handles.add(handle);
		}
		return new SharedRef<X>(this, handle, (X) entry.object);
	}

	protected Entry<T> doCreateObjByDef(TDef def) {
		SharedObjectByDefFactory<TDef,T> factory = findMatchingFactoryFor(def);
		if (factory == null) {
			throw new UnsupportedOperationException("Factory not found for def: " + def);
		}
		String displayName = ""; // TODO
		List<SharedRef<?>> dependencyRefs = new ArrayList<SharedRef<?>>();
		DependencyObjectCreationContext ctx = new DependencyObjectCreationContext(owner, dependencyRefs, displayName);

		// *** the biggy ***
		T obj = factory.create(def, ctx);
		
		dependencyRefs = dependencyRefs.isEmpty()? ImmutableList.copyOf(dependencyRefs) : null;
		return new Entry<T>(obj, dependencyRefs);
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
		if (entry.dependencyRefs != null && !entry.dependencyRefs.isEmpty()) {
			for(SharedRef<?> depRef : entry.dependencyRefs) {
				depRef.close();
			}
			entry.dependencyRefs = null;
		}
		T obj = entry.object;
		entry.object = null;
		if (obj instanceof Closeable) {
			try {
				((Closeable) obj).close();
			} catch(Exception ex) {
				LOG.warn("Failed to close properly " + entry.object + " ... ignore, no rethrow!");
			}
		}
	}
	
	
	public SharedObjectByDefFactory<TDef,T> findMatchingFactoryFor(TDef def) {
		SharedObjectByDefFactory<TDef,T> res = null;
		for(SharedObjectByDefFactory<TDef,T> f : registeredFactories) {
			if (f.accepts(def)) {
				res = f;
				break;
			}
		}
		return res;
	}

	public void registerFactory(SharedObjectByDefFactory<TDef,T> factory) {
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

	
	// ------------------------------------------------------------------------
	
	private static final class DefKeyPair<TDef> { // yet another 1000th Pair<A,B> class !!!
		private final TDef def;
		private final Object key;
		
		public DefKeyPair(TDef def, Object key) {
			this.key = key;
			this.def = def;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((def == null) ? 0 : def.hashCode());
			result = prime * result + ((key == null) ? 0 : key.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			@SuppressWarnings("unchecked")
			DefKeyPair<TDef> other = (DefKeyPair<TDef>) obj;
			if (def == null) {
				if (other.def != null)
					return false;
			} else if (!def.equals(other.def))
				return false;
			if (key == null) {
				if (other.key != null)
					return false;
			} else if (!key.equals(other.key))
				return false;
			return true;
		}
		
	}
	
	private static final class Entry<T> {
		T object;

		Set<Handle> handles = new HashSet<Handle>();
		
		List<SharedRef<?>> dependencyRefs;

		public Entry(T object, List<SharedRef<?>> dependencyRefs) {
			this.object = object;
			this.dependencyRefs = dependencyRefs;
		}
		
		
	}
	

}
