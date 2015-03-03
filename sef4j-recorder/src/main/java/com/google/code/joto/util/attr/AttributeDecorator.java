package com.google.code.joto.util.attr;

import java.util.IdentityHashMap;
import java.util.Map;

public interface AttributeDecorator<T,K,V> {

	boolean containsAttr(T target, K key);
	V getAttr(T target, K key);
	V putAttr(T target, K key, V value);
	
	
	// ------------------------------------------------------------------------
	
	/**
	 * 
	 */
	public static abstract class AbstractAttributeDecorator<T,K,V> implements AttributeDecorator<T,K,V> {


		protected AbstractAttributeDecorator() {
		}

		public boolean containsAttr(T target, K key) {
			IAttributeSupport attrSupport = getAttrSupport(target);
			return attrSupport.containsAttr(key);
		}

		@SuppressWarnings("unchecked")
		public V getAttr(T target, K key) {
			IAttributeSupport attrSupport = getAttrSupport(target);
			return (V) attrSupport.getAttr(key);
		}

		@SuppressWarnings("unchecked")
		public V putAttr(T target, K key, V value) {
			IAttributeSupport attrSupport = getAttrSupport(target);
			return (V) attrSupport.putAttr(key, value);
		}
		
		protected abstract IAttributeSupport getAttrSupport(Object target);
		
	}
	
	/**
	 * 
	 */
	public static class ExternalAttributeDecorator<T,K,V> extends AbstractAttributeDecorator<T,K,V> {

		private Map<Object,IAttributeSupport> identityAttributeSupport =
			new IdentityHashMap<Object,IAttributeSupport>();
		
		public ExternalAttributeDecorator() {
		}
		
		protected IAttributeSupport getAttrSupport(Object target) {
			IAttributeSupport attrSupport = identityAttributeSupport.get(target);
			if (attrSupport == null) {
				attrSupport = new DefaultAttributeSupport();
				identityAttributeSupport.put(target, attrSupport);
			}
			return attrSupport;
		}
	}
	

	// ------------------------------------------------------------------------
	
	/**
	 * 
	 */
	public static class AttributeSupportDecorator<T,K,V> extends AbstractAttributeDecorator<T,K,V> {

		public AttributeSupportDecorator() {
		}
		
		protected IAttributeSupport getAttrSupport(Object target) {
			return (IAttributeSupport) target;
		}
	}
	

	// ------------------------------------------------------------------------
	
	/**
	 * 
	 */
	public static class AttributeSupportDelegateDecorator<T,K,V> extends AbstractAttributeDecorator<T,K,V> {

		public AttributeSupportDelegateDecorator() {
		}
		
		protected IAttributeSupport getAttrSupport(Object target) {
			IAttributeSupportDelegate target2 = (IAttributeSupportDelegate) target;
			return target2.getAttributeSupport();
		}
	}
	
}
