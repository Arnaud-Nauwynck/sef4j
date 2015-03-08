package org.sef4j.callstack.export.printer;

import java.io.PrintWriter;

import org.sef4j.callstack.stattree.CallTreeNode;

/**
 * 
 */
public abstract class CallTreePrinter {

    protected PrintWriter out;

    // ------------------------------------------------------------------------

    protected CallTreePrinter(PrintWriter out) {
        this.out = out;
    }
    
    // ------------------------------------------------------------------------
    
    public abstract void recursivePrintNodes(CallTreeNode node, int maxDepth);
    
}
