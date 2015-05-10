package org.sef4j.core.util.factorydef;


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
	public abstract T create(TDef def, ObjectByDefRepositories repositories);

	// ------------------------------------------------------------------------
	
	public String toString() {
		return displayName;
	}
	
}
