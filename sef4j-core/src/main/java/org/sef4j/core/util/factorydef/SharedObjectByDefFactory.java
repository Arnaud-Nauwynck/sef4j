package org.sef4j.core.util.factorydef;


public interface SharedObjectByDefFactory<TDef,T> {

	public boolean accepts(TDef def);
	
	public abstract T create(TDef def, DependencyObjectCreationContext ctx);

}
