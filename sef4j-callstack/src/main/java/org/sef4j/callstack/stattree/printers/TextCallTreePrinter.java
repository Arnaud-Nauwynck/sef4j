package org.sef4j.callstack.stattree.printers;

import java.io.PrintWriter;

import org.sef4j.core.helpers.proptree.model.PropTreeNode;
import org.sef4j.core.helpers.proptree.printers.AbstractIndentPropTreePrinter;

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
public class TextCallTreePrinter extends AbstractIndentPropTreePrinter {

    private boolean useOpenClose; // FOR debug only?
    
    // ------------------------------------------------------------------------
    
    protected TextCallTreePrinter(PrintWriter out, Builder builder) {
        super(out, builder);
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
    public void printNodeHeader(PropTreeNode node) {
        print(node.getName());
        if (useOpenClose) print(" {");
    }

    @Override
    public void printNodeFooter(PropTreeNode node) {
        if (useOpenClose) print("}");
    }

    @Override
    protected void printIndentNodeValueListHeader(PropTreeNode node) {
        // do nothing
    }
    @Override
    protected void printIndentNodeValueListFooter(PropTreeNode node) {
        // do nothing
    }
    
    @Override
    public void printNodeValueListHeader(PropTreeNode node) {
        if (useOpenClose) print("propsMap: {");
    }

    @Override
    public void printNodeValueListFooter(PropTreeNode node) {
        if (useOpenClose) print("}");
    }

    @Override
    public void printNodeValueHeader(PropTreeNode node, String propName) {
        printCurrIndent();
        // print(propName + ": "); ... cf already printed with value format printer!
    }

    @Override
    public void printNodeValueFooter(PropTreeNode node, String propName) {
        println();
    }

    @Override
    protected void printIndentChildListHeader(PropTreeNode node) {
        // do nothing
    }
    @Override
    protected void printIndentChildListFooter(PropTreeNode node) {
        // do nothing
    }

    @Override
    public void printChildListHeader(PropTreeNode node) {
        if (useOpenClose) print("childList: [");
    }

    @Override
    public void printChildListFooter(PropTreeNode node) {
        if (useOpenClose) print("]");
    }

    protected void printIndentNodeHeader(PropTreeNode node) {
        super.printIndentNodeHeader(node);
    }
    protected void printIndentNodeFooter(PropTreeNode node) {
        decrIndent();
    }

    @Override
    public void printChildHeader(PropTreeNode node) {
    }
    @Override
    public void printChildFooter(PropTreeNode node) {
        if (useOpenClose) print(",");
    }
    
    // ------------------------------------------------------------------------

    public static class Builder extends AbstractIndentPropTreePrinter.Builder {
        
        public Builder() {
        }

        public TextCallTreePrinter build(PrintWriter out) {
            return new TextCallTreePrinter(out, this);
        }
    }
    
}
