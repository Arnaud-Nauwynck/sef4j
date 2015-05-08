package org.sef4j.core.util.factorydef;


public interface ObjectByDefFactory<TDef,T> {

	public boolean accepts(TDef def);
	
	public T create(TDef def, ObjectByDefRepository<TDef,?,T> repository);
	
}
