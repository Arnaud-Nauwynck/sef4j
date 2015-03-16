package org.sef4j.core.helpers.adapters;

import java.util.HashMap;
import java.util.Map;

/**
 * similar in concept to eclipse IAdaptable ... 
 *
 */
public class AdapterFactoryRegistry {

    /**
     * adapter Interface -> [adaptee object ClassHierarchy --> AdapterFactory]
     */
    private Map<Class<?>,TypeHierarchyToObjectMap<AdapterFactory<?,?>>> interfaceToPerTypeHierarchyAdapterFactory = 
            new HashMap<Class<?>,TypeHierarchyToObjectMap<AdapterFactory<?,?>>>();
    
    // ------------------------------------------------------------------------

    public AdapterFactoryRegistry() {
    }

    // ------------------------------------------------------------------------

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public <TInterface,TAdapt> void registerAdapterFactory(Class<TInterface> interfaceClass, Class<TAdapt> adapteeClass, AdapterFactory<TInterface,TAdapt> adapterFactory) {
        TypeHierarchyToObjectMap<AdapterFactory<TInterface,?>> adapterFactoryMap = (TypeHierarchyToObjectMap)
                interfaceToPerTypeHierarchyAdapterFactory.get(interfaceClass);
        if (adapterFactoryMap == null) {
            adapterFactoryMap = new TypeHierarchyToObjectMap<AdapterFactory<TInterface,?>>();
            interfaceToPerTypeHierarchyAdapterFactory.put((Class)interfaceClass, (TypeHierarchyToObjectMap) adapterFactoryMap);
        }
        adapterFactoryMap.putOverride(adapteeClass, adapterFactory);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public <TInterface,TAdapt> void unregisterAdapterFactory(Class<TInterface> interfaceClass, Class<TAdapt> adapteeClass) {
        TypeHierarchyToObjectMap<AdapterFactory<TInterface,?>> adapterFactoryMap =  (TypeHierarchyToObjectMap)
                interfaceToPerTypeHierarchyAdapterFactory.get(interfaceClass);
        if (adapterFactoryMap != null) {
            adapterFactoryMap.removeOverride(adapteeClass);
        }
    }

    // ------------------------------------------------------------------------
    
    @SuppressWarnings({ "unchecked" })
    public <TInterface,T> TInterface getAdapter(T object, Class<TInterface> interfaceClass) {
        if (object == null) return null;
        TInterface res;
        Class<?> objectClass = object.getClass();
        if (interfaceClass.isAssignableFrom(objectClass)) {
            res = (TInterface) object; // object implements interface => no need for adapter
        } else {
            TypeHierarchyToObjectMap<AdapterFactory<?,?>> adapterFactoryMap = interfaceToPerTypeHierarchyAdapterFactory.get(interfaceClass);
            AdapterFactory<TInterface,T> adapterFactory = (AdapterFactory<TInterface,T>) adapterFactoryMap.get(objectClass);
            if (adapterFactory != null) {
                res = adapterFactory.getAdapter(object);
            } else {
                res = null;
            }
        }
        return res;
    }

}
