package org.sef4j.core.helpers.proptree.printers;

import java.io.PrintWriter;

import org.sef4j.core.helpers.proptree.model.PropTreeNode;

/**
 * 
 */
public interface PropTreeValuePrinter<T> {
    
    public abstract void printValue(PrintWriter output, PropTreeNode node, String propName, T propValue); 

}
