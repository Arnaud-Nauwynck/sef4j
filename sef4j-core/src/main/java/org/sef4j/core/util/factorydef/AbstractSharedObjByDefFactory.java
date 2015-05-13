package org.sef4j.core.util.factorydef;


/**
 * abstract helper base-class for ObjectByDefFactory<TDef,T>
 * 
 * @param <TDef>
 * @param <T>
 */
public abstract class AbstractSharedObjByDefFactory<TDef,T> implements SharedObjectByDefFactory<TDef,T> {

	private final String displayName;
	private final Class<TDef> defClass;
	
	// ------------------------------------------------------------------------
	
	public AbstractSharedObjByDefFactory(String displayName, Class<TDef> defClass) {
		this.displayName = displayName;
		this.defClass = defClass;
	}

	// ------------------------------------------------------------------------
	
	@Override
	public boolean accepts(TDef def) {
		return defClass.isInstance(def);
	}

	@Override
	public abstract T create(TDef def, DependencyObjectCreationContext ctx);

	// ------------------------------------------------------------------------
	
	public String toString() {
		return displayName;
	}
	
}
