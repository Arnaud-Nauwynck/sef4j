package com.google.code.joto.util.attr;


public interface IAttributeSupport {

	public boolean containsAttr(Object key);
	public Object getAttr(Object key);
	public Object getAttr(Object key, Object defaultIfNotFound);
	public <T> T getAttrOrPutNewInstance(Object key, Class<T> classToInstanciante);
	public Object putAttr(Object key, Object value) ;
	public Object removeAttr(Object key);
	
}
