package org.sef4j.callstack.export.printer.helpers;

import java.io.PrintWriter;
import java.util.Map;

import org.sef4j.callstack.export.printer.CallTreePrinter;
import org.sef4j.callstack.export.valueprinter.CallTreeValuePrinter;

/**
 * 
 */
public abstract class AbstractCallTreePrinter extends CallTreePrinter {

    protected Map<String,CallTreeValuePrinter<?>> propPerNamePrinter;
    protected Map<Class<?>,CallTreeValuePrinter<?>> propPerTypePrinter;
    protected CallTreeValuePrinter<?> propDefaultPrinter;
    
    // ------------------------------------------------------------------------

    protected AbstractCallTreePrinter(PrintWriter out, Builder builder) {
        super(out);
        this.propPerNamePrinter = builder.propPerNamePrinter;
        this.propPerTypePrinter = builder.propPerTypePrinter;
        this.propDefaultPrinter = builder.propDefaultPrinter;
    }
    
    // ------------------------------------------------------------------------
    
    @SuppressWarnings("unchecked")
    protected CallTreeValuePrinter<Object> resolvePropValuePrinter(String propName, Object propValue) {
        CallTreeValuePrinter<?> valuePrinter = null;
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
        return (CallTreeValuePrinter<Object>) valuePrinter;
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

        protected Map<String,CallTreeValuePrinter<?>> propPerNamePrinter;
        protected Map<Class<?>,CallTreeValuePrinter<?>> propPerTypePrinter;
        protected CallTreeValuePrinter<?> propDefaultPrinter;


        public Builder withPropPerNamePrinter(Map<String, CallTreeValuePrinter<?>> propPerNamePrinter) {
            this.propPerNamePrinter = propPerNamePrinter;
            return this;
        }

        public Builder withPropPerTypePrinter(Map<Class<?>, CallTreeValuePrinter<?>> propPerTypePrinter) {
            this.propPerTypePrinter = propPerTypePrinter;
            return this;
        }

        public Builder withPropDefaultPrinter(CallTreeValuePrinter<?> propDefaultPrinter) {
            this.propDefaultPrinter = propDefaultPrinter;
            return this;
        }
        
    }
    
}
