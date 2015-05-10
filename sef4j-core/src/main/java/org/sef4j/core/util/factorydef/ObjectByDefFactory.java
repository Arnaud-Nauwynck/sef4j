package org.sef4j.core.util.factorydef;


public interface ObjectByDefFactory<TDef,T> {

	public boolean accepts(TDef def);
	
	public abstract T create(TDef def, ObjectByDefRepository<TDef,?> repository);

}
