package org.sef4j.callstack.export.printer.helpers;

import java.io.PrintWriter;

import org.sef4j.callstack.stattree.CallTreeNode;

/**
 * CallTreePrinter to print as indented ASCII, like
 * <PRE>
| a1
| | histo1:{count0: 0, sum0: 0, count1: 0, sum1: 0, count2: 0, sum2: 0, count3: 0, sum3: 0, count4: 0, sum4: 0, count5: 0, sum5: 0, count6: 0, sum6: 0, count7: 0, sum7: 0, count8: 0, sum8: 0, count9: 0, sum9: 0}
| | pending1:{pendingCount: 0, pendingSumStartTime: 0}
| | perfStat1:{count0: 0, sum0: 0, count1: 0, sum1: 0, count2: 0, sum2: 0, count3: 0, sum3: 0, count4: 0, sum4: 0, count5: 0, sum5: 0, count6: 0, sum6: 0, count7: 0, sum7: 0, count8: 0, sum8: 0, count9: 0, sum9: 0}
| | b1
| | | histo1:{count0: 0, sum0: 0, count1: 0, sum1: 0, count2: 0, sum2: 0, count3: 0, sum3: 0, count4: 0, sum4: 0, count5: 0, sum5: 0, count6: 0, sum6: 0, count7: 0, sum7: 0, count8: 0, sum8: 0, count9: 0, sum9: 0}
| | | pending1:{pendingCount: 0, pendingSumStartTime: 0}
| | | perfStat1:{count0: 0, sum0: 0, count1: 0, sum1: 0, count2: 0, sum2: 0, count3: 0, sum3: 0, count4: 0, sum4: 0, count5: 0, sum5: 0, count6: 0, sum6: 0, count7: 0, sum7: 0, count8: 0, sum8: 0, count9: 0, sum9: 0}
| | | c1
| | | | histo1:{count0: 0, sum0: 0, count1: 0, sum1: 0, count2: 0, sum2: 0, count3: 0, sum3: 0, count4: 0, sum4: 0, count5: 0, sum5: 0, count6: 0, sum6: 0, count7: 0, sum7: 0, count8: 0, sum8: 0, count9: 0, sum9: 0}
| | | | pending1:{pendingCount: 0, pendingSumStartTime: 0}
| | | | perfStat1:{count0: 0, sum0: 0, count1: 0, sum1: 0, count2: 0, sum2: 0, count3: 0, sum3: 0, count4: 0, sum4: 0, count5: 0, sum5: 0, count6: 0, sum6: 0, count7: 0, sum7: 0, count8: 0, sum8: 0, count9: 0, sum9: 0}
</PRE>
 */
public class TextCallTreePrinter extends AbstractIndentCallTreePrinter {

    private boolean useOpenClose;
    
    // ------------------------------------------------------------------------
    
    protected TextCallTreePrinter(Builder builder) {
        super(builder);
    }

    // ------------------------------------------------------------------------

    protected void printCurrIndent() {
        for(int i = 0; i < currIndentLevel; i+=2) {
            out.print('|');
            if (i + 1 < currIndentLevel) {
                out.print(' ');
            }
        }
    }

    
    
    @Override
    public void printNodeHeader(CallTreeNode node) {
        print(node.getName());
        if (useOpenClose) print(" {");
    }

    @Override
    public void printNodeFooter(CallTreeNode node) {
        if (useOpenClose) print("}");
    }

    @Override
    protected void printIndentNodeValueListHeader(CallTreeNode node) {
        // do nothing
    }
    @Override
    protected void printIndentNodeValueListFooter(CallTreeNode node) {
        // do nothing
    }
    
    @Override
    public void printNodeValueListHeader(CallTreeNode node) {
        if (useOpenClose) print("propsMap: {");
    }

    @Override
    public void printNodeValueListFooter(CallTreeNode node) {
        if (useOpenClose) print("}");
    }

    @Override
    public void printNodeValueHeader(CallTreeNode node, String propName) {
        printCurrIndent();
        // print(propName + ": "); ... cf already printed with value format printer!
    }

    @Override
    public void printNodeValueFooter(CallTreeNode node, String propName) {
        println();
    }

    @Override
    protected void printIndentChildListHeader(CallTreeNode node) {
        // do nothing
    }
    @Override
    protected void printIndentChildListFooter(CallTreeNode node) {
        // do nothing
    }

    @Override
    public void printChildListHeader(CallTreeNode node) {
        if (useOpenClose) print("childList: [");
    }

    @Override
    public void printChildListFooter(CallTreeNode node) {
        if (useOpenClose) print("]");
    }

    protected void printIndentNodeHeader(CallTreeNode node) {
        super.printIndentNodeHeader(node);
    }
    protected void printIndentNodeFooter(CallTreeNode node) {
        decrIndent();
    }

    @Override
    public void printChildHeader(CallTreeNode node) {
    }
    @Override
    public void printChildFooter(CallTreeNode node) {
        if (useOpenClose) print(",");
    }
    
    // ------------------------------------------------------------------------

    public static class Builder extends AbstractIndentCallTreePrinter.Builder {
        
        public Builder(PrintWriter out) {
            super(out);
        }

        public TextCallTreePrinter build() {
            return new TextCallTreePrinter(this);
        }
    }
    
}
