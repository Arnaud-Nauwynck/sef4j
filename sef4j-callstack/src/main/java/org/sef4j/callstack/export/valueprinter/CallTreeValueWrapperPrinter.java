package org.sef4j.callstack.export.valueprinter;

import java.io.PrintWriter;

import org.sef4j.callstack.stattree.CallTreeNode;

/**
 * adapter implements CallTreeValuePrinter, delegate to ValuePrinter.
 *  (CallTree,name,Object) -> String text 
 *
 * using flags, you can format to "<<value>>" or "<<propName>> : { <<value>> }", 
 * or more generally:  "<<propName>> <<prefixSep>> <<value>> <<postfixSep>>"
 *  
 * @param <T>
 */
public class CallTreeValueWrapperPrinter<T> implements CallTreeValuePrinter<T> {
    
    private final ValuePrinter<T> delegate;
    private final boolean prefixPropName;
    private final String prefixSep;
    private final String postfixSep;
    
    public CallTreeValueWrapperPrinter(ValuePrinter<T> delegate, boolean prefixPropName, String prefixSep, String postfixSep) {
        super();
        this.delegate = delegate;
        this.prefixPropName = prefixPropName;
        this.prefixSep = prefixSep;
        this.postfixSep = postfixSep;
    }

    public void printValue(PrintWriter output, CallTreeNode node, String propName, T propValue) {
        if (prefixPropName) {
            output.print(propName);
        }
        if (prefixSep != null) {
            output.print(prefixSep);
        }
        delegate.printValue(output, propValue);
        if (postfixSep != null) {
            output.print(postfixSep);
        }
    }
    
}
