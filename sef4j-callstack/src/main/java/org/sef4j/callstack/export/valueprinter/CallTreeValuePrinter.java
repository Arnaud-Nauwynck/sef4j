package org.sef4j.callstack.export.valueprinter;

import java.io.PrintWriter;

import org.sef4j.callstack.stattree.CallTreeNode;

/**
 * 
 */
public interface CallTreeValuePrinter<T> {
    
    public abstract void printValue(PrintWriter output, CallTreeNode node, String propName, T propValue); 

}
