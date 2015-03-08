package org.sef4j.callstack.export.printer;

import org.sef4j.callstack.stattree.CallTreeNode;

/**
 * 
 */
public abstract class CallTreeValuePrinter {

    protected final CallTreePrinter delegate;

    // ------------------------------------------------------------------------

    public CallTreeValuePrinter(CallTreePrinter delegate) {
        this.delegate = delegate;
    }

    // ------------------------------------------------------------------------
    
    public abstract void printValue(CallTreeNode node, String propName, Object propValue); 
    
    
    protected void printText(String text) {
        delegate.out.print(text);
    }
    
}
