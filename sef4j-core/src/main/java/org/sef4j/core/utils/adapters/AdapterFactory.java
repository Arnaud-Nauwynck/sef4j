package org.sef4j.core.utils.adapters;

public interface AdapterFactory<TInterface, TAdapted> {

    public TInterface getAdapter(TAdapted obj);

}