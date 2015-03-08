package org.sef4j.callstack.export.printer.helpers;

import java.io.PrintWriter;

import org.sef4j.callstack.stattree.CallTreeNode;

/**
 * 
 */
public class TextCallTreePrinter extends AbstractIndentCallTreePrinter {

    // ------------------------------------------------------------------------
    
    protected TextCallTreePrinter(Builder builder) {
        super(builder);
    }

    // ------------------------------------------------------------------------

    @Override
    public void printNodeHeader(CallTreeNode node) {
        print(node.getName() + " {");
    }

    @Override
    public void printNodeFooter(CallTreeNode node) {
        if (useIndent) {
            print("} /* " + node.getName() + " */");
        } else {
            print("}");
        }
    }

    @Override
    public void printNodeValueListHeader(CallTreeNode node) {
        print("propsMap: {");
    }

    @Override
    public void printNodeValueListFooter(CallTreeNode node) {
        print("}");
    }

    @Override
    public void printNodeValueHeader(CallTreeNode node, String propName) {
        print("propName: ");
    }

    @Override
    public void printNodeValueFooter(CallTreeNode node, String propName) {
        // do nothing
    }

    @Override
    public void printChildListHeader(CallTreeNode node) {
        print("childList: [");
        int childListSize = node.getChildList().size();
        if (childListSize > 2) {
            print("/* " + childListSize + " child */");
        }
    }

    @Override
    public void printChildListFooter(CallTreeNode node) {
        print("]");
    }

    @Override
    public void printChildHeader(CallTreeNode node) {
    }

    @Override
    public void printChildFooter(CallTreeNode node) {
        // print(",");
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
