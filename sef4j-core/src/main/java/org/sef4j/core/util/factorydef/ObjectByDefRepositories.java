package org.sef4j.core.util.factorydef;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * repositories of object created by definition
 * = composite of Repositories by type 
 */
public class ObjectByDefRepositories {

	private static final Logger LOG = LoggerFactory.getLogger(ObjectByDefRepositories.class);
	
	private Map<Class<?>,ObjectByDefRepository<?,?>> repositories = new HashMap<Class<?>,ObjectByDefRepository<?,?>>();
	
	// private Object lock = new Object();
	
	// ------------------------------------------------------------------------
	
	public ObjectByDefRepositories() {
	}

	// ------------------------------------------------------------------------

	@SuppressWarnings("unchecked")
	public <TDef,T> ObjectByDefRepository<TDef,T> getOrCreateRepositoryForClass(Class<TDef> clss) {
		ObjectByDefRepository<?,?> res = repositories.get(clss);
		if (res == null) {
			res = new ObjectByDefRepository<TDef,T>(this, clss.getCanonicalName());
			repositories.put(clss, res);
		}
		return (ObjectByDefRepository<TDef,T>) res;
	}
	
	public <TDef,T> void registerFactoryFor(Class<TDef> clss, ObjectByDefFactory<TDef,T> factory) {
		ObjectByDefRepository<TDef,T> repo = getOrCreateRepositoryForClass(clss);
		repo.registerFactory(factory);
	}

	
	public ObjectByDefRepository<?,?> repositoryForClass(Class<?> clss) {
		ObjectByDefRepository<?, ?> res = repositories.get(clss);
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
	public <TDef,T> ObjectWithHandle<T> getOrCreateByDef(TDef def) {
		Class<?> clss = def.getClass();
		ObjectByDefRepository<?, ?> repoObj = repositoryForClass(clss);
		@SuppressWarnings("unchecked")
		ObjectByDefRepository<TDef,T> repo = (ObjectByDefRepository<TDef,T>) repoObj;
		ObjectWithHandle<T> res = repo.getOrCreateByDef(def);
		return res;
	}

	/**
	 * helper downcast for <code>getOrCreateByDef(def)</code>
	 */
	public <TDef,T> ObjectWithHandle<T> getOrCreateByDef(TDef def, Class<T> clss) {
		ObjectWithHandle<T> res = getOrCreateByDef(def);
		if (clss != null && !clss.isInstance(res.getObject())) {
			throw new ClassCastException();
		}
		return res;
	}
	
	// ------------------------------------------------------------------------

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ObjectByDefRepositories[");
		for(ObjectByDefRepository<?,?> repo : repositories.values()) {
			sb.append(repo.toString());
			sb.append("\n");
		}
		sb.append("]");
		return sb.toString();
	}
	
}
