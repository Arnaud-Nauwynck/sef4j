package org.sef4j.core.util.factorydef;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * repositories of object created by definition
 * = composite of Repositories by type 
 */
public class ObjectByDefRepositories {

	private static final Logger LOG = LoggerFactory.getLogger(ObjectByDefRepositories.class);
	
	private Map<Class<?>,SharedObjectByDefRepository<?,?>> repositories = new HashMap<Class<?>,SharedObjectByDefRepository<?,?>>();
	
	// private Object lock = new Object();
	
	// ------------------------------------------------------------------------
	
	public ObjectByDefRepositories() {
	}

	public void close() {
		for(SharedObjectByDefRepository<?,?> repository : repositories.values()) {
			repository.close();
		}
		repositories = null;
	}
	
	// ------------------------------------------------------------------------

	@SuppressWarnings("unchecked")
	public <TDef,T> SharedObjectByDefRepository<TDef,T> getOrCreateRepositoryForClass(Class<TDef> clss) {
		SharedObjectByDefRepository<?,?> res = repositories.get(clss);
		if (res == null) {
			res = new SharedObjectByDefRepository<TDef,T>(this, clss.getCanonicalName());
			repositories.put(clss, res);
		}
		return (SharedObjectByDefRepository<TDef,T>) res;
	}
	
	public <TDef,T> void registerFactoryFor(Class<TDef> clss, SharedObjectByDefFactory<TDef,T> factory) {
		SharedObjectByDefRepository<TDef,T> repo = getOrCreateRepositoryForClass(clss);
		repo.registerFactory(factory);
	}

	
	public SharedObjectByDefRepository<?,?> repositoryForClass(Class<?> clss) {
		SharedObjectByDefRepository<?, ?> res = repositories.get(clss);
		for(Class<?> superClss = clss; ; superClss = superClss.getSuperclass()) {
			res = repositories.get(clss);
			if (res != null) {
				break;
			}
			if (superClss == Object.class) {
				break;
			}
		}
		if (res == null) {
			LOG.warn("no repository for class:" + clss);
			throw new IllegalArgumentException();
		}
		return res;
	}

	/**
	 * register an object by its key definition
	 * utility method for <code>repositoryForClass(def.getClass()).register(def)</code>
	 */
	public <TDef,T> SharedRef<T> getOrCreateByDef(TDef def, Object key) {
		Class<?> clss = def.getClass();
		SharedObjectByDefRepository<?, ?> repoObj = repositoryForClass(clss);
		@SuppressWarnings("unchecked")
		SharedObjectByDefRepository<TDef,T> repo = (SharedObjectByDefRepository<TDef,T>) repoObj;
		SharedRef<T> res = repo.getOrCreateByDef(def, key);
		return res;
	}

	/**
	 * helper downcast for <code>getOrCreateByDef(def)</code>
	 */
	public <TDef,T> SharedRef<T> getOrCreateByDef(TDef def, Object key, Class<T> clss) {
		SharedRef<T> res = getOrCreateByDef(def, key);
		if (clss != null && !clss.isInstance(res.getObject())) {
			throw new ClassCastException();
		}
		return res;
	}

	public <TDef,T> List<SharedRef<T>> getOrCreateByDefs(Collection<TDef> defs) {
		List<SharedRef<T>> res = new ArrayList<SharedRef<T>>();
		for(TDef def : defs) {
			SharedRef<T> resElt = getOrCreateByDef(def, null);
			res.add(resElt);
		}
		return res;
	}
	
	// ------------------------------------------------------------------------

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ObjectByDefRepositories[\n");
		for(SharedObjectByDefRepository<?,?> repo : repositories.values()) {
			sb.append(repo.toString());
			sb.append("\n");
		}
		sb.append("]");
		return sb.toString();
	}
	
}
