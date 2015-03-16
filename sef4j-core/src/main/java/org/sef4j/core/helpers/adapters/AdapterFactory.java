package org.sef4j.core.helpers.adapters;

public interface AdapterFactory<TInterface,TAdapted> {
    
    public TInterface getAdapter(TAdapted obj);
    
}