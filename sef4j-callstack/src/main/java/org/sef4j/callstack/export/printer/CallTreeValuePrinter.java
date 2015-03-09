package org.sef4j.callstack.export.printer;

import org.sef4j.callstack.stattree.CallTreeNode;

/**
 * 
 */
public interface CallTreeValuePrinter<T> {
    
    public void printValue(CallTreePrinter output, CallTreeNode node, String propName, T propValue); 

}
