package org.sef4j.core.helpers.proptree.printers;

import java.io.PrintWriter;

import org.sef4j.core.api.proptree.PropTreeNode;
import org.sef4j.core.helpers.exporters.ValuePrinter;
import org.sef4j.core.helpers.proptree.printers.PropTreeValuePrinter;

/**
 * adapter implements PropTreeValuePrinter, delegate to ValuePrinter.
 *  (PropTree,name,Object) -> String text 
 *
 * using flags, you can format to "<<value>>" or "<<propName>> : { <<value>> }", 
 * or more generally:  "<<propName>> <<prefixSep>> <<value>> <<postfixSep>>"
 *  
 * @param <T>
 */
public class PropTreeValueWrapperPrinter<T> implements PropTreeValuePrinter<T> {
    
    private final ValuePrinter<T> delegate;
    private final boolean prefixPropName;
    private final String prefixSep;
    private final String postfixSep;
    
    public PropTreeValueWrapperPrinter(ValuePrinter<T> delegate, boolean prefixPropName, String prefixSep, String postfixSep) {
        super();
        this.delegate = delegate;
        this.prefixPropName = prefixPropName;
        this.prefixSep = prefixSep;
        this.postfixSep = postfixSep;
    }

    public void printValue(PrintWriter output, PropTreeNode node, String propName, T propValue) {
        if (prefixPropName) {
            output.print(propName);
        }
        if (prefixSep != null) {
            output.print(prefixSep);
        }
        delegate.printValue(output, propName, propValue);
        if (postfixSep != null) {
            output.print(postfixSep);
        }
    }
    
}
