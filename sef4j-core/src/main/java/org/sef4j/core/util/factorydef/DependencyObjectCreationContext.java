package org.sef4j.core.util.factorydef;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * a temporary context used at creation time, to keep track of dependency refs created 
 */
public class DependencyObjectCreationContext {

	private ObjectByDefRepositories repositories;

	private List<String> currDisplayPathName = new ArrayList<String>();
	
	private List<SharedRef<?>> dependencyRefs;

	// ------------------------------------------------------------------------
	
	public DependencyObjectCreationContext(ObjectByDefRepositories repositories, List<SharedRef<?>> dependencyRefs, String displayRootObjectName) {
		this.repositories = repositories;
		this.dependencyRefs = dependencyRefs;
		currDisplayPathName.add(displayRootObjectName);
	}
	
	// ------------------------------------------------------------------------

	public <TDef,T> T getOrCreateDependencyByDef(String dependencyDisplayName, TDef def, Object key) {
		try (InnerCloseable toPop = withPushDependencyDisplayName(dependencyDisplayName)) {
			SharedRef<T> tmpres = repositories.getOrCreateByDef(def, key);
			addDependencyRef(tmpres);
			return tmpres.getObject();
		}
	}

	/** helper for <code>getOrCreateDependencyByDef(,,null)</code> */
	public <TDef,T> T getOrCreateDependencyByDef(String dependencyDisplayName, TDef def) {
		return getOrCreateDependencyByDef(dependencyDisplayName, def, null);
	}

	public <TDef,T> List<T> getOrCreateDependencyByDefs(String dependencyDisplayName, Collection<TDef> defs) {
		try (InnerCloseable toPop = withPushDependencyDisplayName(dependencyDisplayName)) {
			List<T> res = new ArrayList<T>(defs.size());
			List<SharedRef<T>> tmpres = repositories.getOrCreateByDefs(defs);
			for(SharedRef<T> depRef : tmpres) {
				addDependencyRef(depRef);
				res.add(depRef.getObject());
			}
			return res;
		}
	}

	
	public void addDependencyRef(SharedRef<?> ref) {
		dependencyRefs.add(ref);
	}

	
	public String getCurrObjectDisplayName() {
		return String.join(".", currDisplayPathName);
	}

	public InnerCloseable withPushDependencyDisplayName(String name) {
		currDisplayPathName.add(name);
		return new InnerCloseable();
	}

	/** internal helper, for <code>pus() try { ..} finally { pop() }</code> */
	protected class InnerCloseable implements Closeable {
		public void close() {
			currDisplayPathName.remove(currDisplayPathName.size() - 1);
		}
	}
}
