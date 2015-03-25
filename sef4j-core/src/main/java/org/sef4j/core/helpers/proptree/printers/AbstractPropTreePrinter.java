package org.sef4j.core.helpers.proptree.printers;

import java.io.PrintWriter;
import java.util.Map;

/**
 * 
 */
public abstract class AbstractPropTreePrinter extends PropTreePrinter {

    protected Map<String,PropTreeValuePrinter<?>> propPerNamePrinter;
    protected Map<Class<?>,PropTreeValuePrinter<?>> propPerTypePrinter;
    protected PropTreeValuePrinter<?> propDefaultPrinter;
    
    // ------------------------------------------------------------------------

    protected AbstractPropTreePrinter(PrintWriter out, Builder builder) {
        super(out);
        this.propPerNamePrinter = builder.propPerNamePrinter;
        this.propPerTypePrinter = builder.propPerTypePrinter;
        this.propDefaultPrinter = builder.propDefaultPrinter;
    }
    
    // ------------------------------------------------------------------------
    
    @SuppressWarnings("unchecked")
    protected PropTreeValuePrinter<Object> resolvePropValuePrinter(String propName, Object propValue) {
        PropTreeValuePrinter<?> valuePrinter = null;
        if (propPerNamePrinter != null) {
            valuePrinter = propPerNamePrinter.get(propName);
        }
        if (valuePrinter == null 
                && propPerTypePrinter != null && !propPerTypePrinter.isEmpty()) {
            // search by type
            Class<?> propClss = propValue.getClass();
            valuePrinter = propPerTypePrinter.get(propClss);
            // when not found, find by parent class type... until Object.class
            while(valuePrinter == null && propClss != Object.class) {
                propClss = propClss.getSuperclass();
                valuePrinter = propPerTypePrinter.get(propClss);
            }
        }
        if (valuePrinter == null 
                && propDefaultPrinter != null) {
            propDefaultPrinter = valuePrinter;
        }
        return (PropTreeValuePrinter<Object>) valuePrinter;
    }

    // ------------------------------------------------------------------------
    
    @Override
    public String toString() {
        return "CallTreePrinter["
                + ((propPerNamePrinter != null)? "propNames:" + propPerNamePrinter.keySet() + ", " : "")
                + ((propPerTypePrinter != null)? "propTypes:" + propPerTypePrinter.keySet() + ", " : "")
                + ((propDefaultPrinter != null)? "propDefault, " : "")
                + "]";
    }
    
    // ------------------------------------------------------------------------

    /**
     * Builder design-pattern
     */
    protected static abstract class Builder {

        protected Map<String,PropTreeValuePrinter<?>> propPerNamePrinter;
        protected Map<Class<?>,PropTreeValuePrinter<?>> propPerTypePrinter;
        protected PropTreeValuePrinter<?> propDefaultPrinter;


        public Builder withPropPerNamePrinter(Map<String, PropTreeValuePrinter<?>> propPerNamePrinter) {
            this.propPerNamePrinter = propPerNamePrinter;
            return this;
        }

        public Builder withPropPerTypePrinter(Map<Class<?>, PropTreeValuePrinter<?>> propPerTypePrinter) {
            this.propPerTypePrinter = propPerTypePrinter;
            return this;
        }

        public Builder withPropDefaultPrinter(PropTreeValuePrinter<?> propDefaultPrinter) {
            this.propDefaultPrinter = propDefaultPrinter;
            return this;
        }
        
    }
    
}
