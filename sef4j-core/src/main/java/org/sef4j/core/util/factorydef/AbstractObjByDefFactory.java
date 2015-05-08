package org.sef4j.core.util.factorydef;

import org.sef4j.core.util.factorydef.ObjectByDefRepository;
import org.sef4j.core.util.factorydef.ObjectByDefFactory;

/**
 * abstract helper base-class for ObjectByDefFactory<TDef,T>
 * 
 * @param <TDef>
 * @param <T>
 */
public abstract class AbstractObjByDefFactory<TDef,T> implements ObjectByDefFactory<TDef,T> {

	private final String displayName;
	
	// ------------------------------------------------------------------------
	
	public AbstractObjByDefFactory(String displayName) {
		this.displayName = displayName;
	}

	// ------------------------------------------------------------------------
	
	@Override
	public abstract boolean accepts(TDef def);

	@Override
	public abstract T create(TDef def, ObjectByDefRepository<TDef,?,T> repository);

	// ------------------------------------------------------------------------
	
	public String toString() {
		return displayName;
	}
	
}
