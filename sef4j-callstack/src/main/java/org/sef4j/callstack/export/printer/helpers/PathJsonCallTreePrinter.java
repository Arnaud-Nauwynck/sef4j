package org.sef4j.callstack.export.printer.helpers;

import java.io.PrintWriter;

import org.sef4j.callstack.stattree.CallTreeNode;

/**
 * CallTreePrinter to print as (indented) JSON, like
 * <PRE>
"a1:histo1" : {count0: 0, sum0: 0, count1: 0, sum1: 0, count2: 0, sum2: 0, count3: 0, sum3: 0, count4: 0, sum4: 0, count5: 0, sum5: 0, count6: 0, sum6: 0, count7: 0, sum7: 0, count8: 0, sum8: 0, count9: 0, sum9: 0}
"a1:pending1" : {pendingCount: 0, pendingSumStartTime: 0}
"a1:perfStat1" : {count0: 0, sum0: 0, count1: 0, sum1: 0, count2: 0, sum2: 0, count3: 0, sum3: 0, count4: 0, sum4: 0, count5: 0, sum5: 0, count6: 0, sum6: 0, count7: 0, sum7: 0, count8: 0, sum8: 0, count9: 0, sum9: 0}
"a1/b1:histo1" : {count0: 0, sum0: 0, count1: 0, sum1: 0, count2: 0, sum2: 0, count3: 0, sum3: 0, count4: 0, sum4: 0, count5: 0, sum5: 0, count6: 0, sum6: 0, count7: 0, sum7: 0, count8: 0, sum8: 0, count9: 0, sum9: 0}
"a1/b1:pending1" : {pendingCount: 0, pendingSumStartTime: 0}
"a1/b1:perfStat1" : {count0: 0, sum0: 0, count1: 0, sum1: 0, count2: 0, sum2: 0, count3: 0, sum3: 0, count4: 0, sum4: 0, count5: 0, sum5: 0, count6: 0, sum6: 0, count7: 0, sum7: 0, count8: 0, sum8: 0, count9: 0, sum9: 0}
"a1/b1/c1:histo1" : {count0: 0, sum0: 0, count1: 0, sum1: 0, count2: 0, sum2: 0, count3: 0, sum3: 0, count4: 0, sum4: 0, count5: 0, sum5: 0, count6: 0, sum6: 0, count7: 0, sum7: 0, count8: 0, sum8: 0, count9: 0, sum9: 0}
"a1/b1/c1:pending1" : {pendingCount: 0, pendingSumStartTime: 0}
"a1/b1/c1:perfStat1" : {count0: 0, sum0: 0, count1: 0, sum1: 0, count2: 0, sum2: 0, count3: 0, sum3: 0, count4: 0, sum4: 0, count5: 0, sum5: 0, count6: 0, sum6: 0, count7: 0, sum7: 0, count8: 0, sum8: 0, count9: 0, sum9: 0}
"a2:histo1" : {count0: 0, sum0: 0, count1: 0, sum1: 0, count2: 0, sum2: 0, count3: 0, sum3: 0, count4: 0, sum4: 0, count5: 0, sum5: 0, count6: 0, sum6: 0, count7: 0, sum7: 0, count8: 0, sum8: 0, count9: 0, sum9: 0}
"a2:pending1" : {pendingCount: 0, pendingSumStartTime: 0}
"a2:perfStat1" : {count0: 0, sum0: 0, count1: 0, sum1: 0, count2: 0, sum2: 0, count3: 0, sum3: 0, count4: 0, sum4: 0, count5: 0, sum5: 0, count6: 0, sum6: 0, count7: 0, sum7: 0, count8: 0, sum8: 0, count9: 0, sum9: 0}
"a2/b1:histo1" : {count0: 0, sum0: 0, count1: 0, sum1: 0, count2: 0, sum2: 0, count3: 0, sum3: 0, count4: 0, sum4: 0, count5: 0, sum5: 0, count6: 0, sum6: 0, count7: 0, sum7: 0, count8: 0, sum8: 0, count9: 0, sum9: 0}
"a2/b1:pending1" : {pendingCount: 0, pendingSumStartTime: 0}
"a2/b1:perfStat1" : {count0: 0, sum0: 0, count1: 0, sum1: 0, count2: 0, sum2: 0, count3: 0, sum3: 0, count4: 0, sum4: 0, count5: 0, sum5: 0, count6: 0, sum6: 0, count7: 0, sum7: 0, count8: 0, sum8: 0, count9: 0, sum9: 0}
"a3:histo1" : {count0: 0, sum0: 0, count1: 0, sum1: 0, count2: 0, sum2: 0, count3: 0, sum3: 0, count4: 0, sum4: 0, count5: 0, sum5: 0, count6: 0, sum6: 0, count7: 0, sum7: 0, count8: 0, sum8: 0, count9: 0, sum9: 0}
"a3:pending1" : {pendingCount: 0, pendingSumStartTime: 0}
"a3:perfStat1" : {count0: 0, sum0: 0, count1: 0, sum1: 0, count2: 0, sum2: 0, count3: 0, sum3: 0, count4: 0, sum4: 0, count5: 0, sum5: 0, count6: 0, sum6: 0, count7: 0, sum7: 0, count8: 0, sum8: 0, count9: 0, sum9: 0}
</PRE>
 */
public class PathJsonCallTreePrinter extends AbstractIndentCallTreePrinter {

    private String currNodePath;
    
    // ------------------------------------------------------------------------
    
    protected PathJsonCallTreePrinter(PrintWriter out, Builder builder) {
        super(out, builder);
    }

    // ------------------------------------------------------------------------

    @Override
    public void printNodeHeader(CallTreeNode node) {
        currNodePath = node.getPathStr();
    }

    @Override
    public void printNodeFooter(CallTreeNode node) {
    }
    
    @Override
    public void printNodeValueListHeader(CallTreeNode node) {
    }

    @Override
    public void printNodeValueListFooter(CallTreeNode node) {
    }

    @Override
    public void printNodeValueHeader(CallTreeNode node, String propName) {
        print("\"" + currNodePath // node.getPathStr()
            + ":" + propName + "\" : ");
    }

    @Override
    public void printNodeValueFooter(CallTreeNode node, String propName) {
        println();
    }

    @Override
    public void printChildListHeader(CallTreeNode node) {
    }

    @Override
    public void printChildListFooter(CallTreeNode node) {
    }

    @Override
    public void printChildHeader(CallTreeNode node) {
    }
    @Override
    public void printChildFooter(CallTreeNode node) {
    }
    
    // ------------------------------------------------------------------------

    public static class Builder extends AbstractIndentCallTreePrinter.Builder {
        
        public Builder() {
            super();
            this.useIndent = false; // ovewrite to skip use of indentation
        }

        public PathJsonCallTreePrinter build(PrintWriter out) {
            return new PathJsonCallTreePrinter(out, this);
        }
    }
    
}
