package org.sef4j.core.helpers.proptree.printers;

import java.io.PrintWriter;

import org.sef4j.core.helpers.proptree.model.PropTreeNode;

/**
 * 
 */
public abstract class PropTreePrinter {

    protected PrintWriter out;

    // ------------------------------------------------------------------------

    protected PropTreePrinter(PrintWriter out) {
        this.out = out;
    }
    
    // ------------------------------------------------------------------------
    
    public abstract void recursivePrintNodes(PropTreeNode node, int maxDepth);
    
}
