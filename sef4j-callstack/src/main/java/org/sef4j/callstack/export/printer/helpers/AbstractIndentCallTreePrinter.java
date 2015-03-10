package org.sef4j.callstack.export.printer.helpers;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Map;

import org.sef4j.callstack.export.valueprinter.CallTreeValuePrinter;
import org.sef4j.callstack.stattree.CallTreeNode;

/**
 * 
 */
public abstract class AbstractIndentCallTreePrinter extends AbstractCallTreePrinter {

    protected final boolean useIndent;
    protected final int indentStep;
    
    protected int currIndentLevel;
    
    // ------------------------------------------------------------------------

    protected AbstractIndentCallTreePrinter(PrintWriter out, Builder builder) {
        super(out, builder);
        this.useIndent = builder.useIndent;
        this.indentStep = builder.indentStep;
    }
    
    // ------------------------------------------------------------------------
    
    public void recursivePrintNodes(CallTreeNode node, int maxDepth) {
        printIndentNodeHeader(node);
        
        // loop to print values 
        printNodeValues(node);
        
        // loop to print child CallTree element
        // **** recursive ****
        if (maxDepth == -1 || maxDepth >= 1) {
            Collection<CallTreeNode> childList = node.getChildList(); // ok to iterate: copy-on-write
            if (childList != null && !childList.isEmpty()) {
                int newMaxDepth = (maxDepth == -1)? -1: maxDepth-1; 
                
                printIndentChildListHeader(node);

                for(CallTreeNode child : childList) {
                    printChildHeader(child);
                    recursivePrintNodes(child, newMaxDepth);
                    printChildFooter(child);
                }
            
                printIndentChildListFooter(node);
            }
        }
        
        printIndentNodeFooter(node);
    }

    public void printNodeValues(CallTreeNode node) {
        Map<String, Object> propsMap = node.getPropsMap();
        if (! propsMap.isEmpty()) {
            printIndentNodeValueListHeader(node);
            
            for(Map.Entry<String,Object> e : propsMap.entrySet()) {
                String propName = e.getKey();
                Object propValue = e.getValue();
                if (propValue == null) {
                    continue; // should not occur?
                }
                // find corresponding ValuePrinter .. filter out if not found
                CallTreeValuePrinter<Object> valuePrinter = resolvePropValuePrinter(propName, propValue);
                if (valuePrinter != null) {
                    printNodeValueHeader(node, propName);
                    valuePrinter.printValue(out, node, propName, propValue);
                    printNodeValueFooter(node, propName);
                }
            }

            printIndentNodeValueListFooter(node);
        }
    }

    // ------------------------------------------------------------------------

    protected void incrIndent(int incr) {
        currIndentLevel += incr;
    }
    protected void incrIndent() {
        incrIndent(+indentStep);
    }
    protected void decrIndent() {
        incrIndent(-indentStep);
    }
    protected void printCurrIndent() {
        for(int i = 0; i < currIndentLevel; i++) {
            out.print(' ');
        }
    }
    protected void println() {
        out.println();
    }
    protected void print(String text) {
        out.print(text);
    }
    protected void println(String text) {
        out.println(text);
    }
    
    public abstract void printNodeHeader(CallTreeNode node);
    public abstract void printNodeFooter(CallTreeNode node);

    public abstract void printNodeValueListHeader(CallTreeNode node);
    public abstract void printNodeValueListFooter(CallTreeNode node);
    
    public abstract void printNodeValueHeader(CallTreeNode node, String propName);
    public abstract void printNodeValueFooter(CallTreeNode node, String propName);
    
    public abstract void printChildListHeader(CallTreeNode node);
    public abstract void printChildListFooter(CallTreeNode node);
    
    public abstract void printChildHeader(CallTreeNode node);
    public abstract void printChildFooter(CallTreeNode node);

    

    protected void printIndentChildListHeader(CallTreeNode node) {
        if (useIndent) {
            printCurrIndent();
        }
        printChildListHeader(node);
        if (useIndent) {
            println();
        }
        incrIndent();
    }

    protected void printIndentChildListFooter(CallTreeNode node) {
        decrIndent();
        if (useIndent) {
            printCurrIndent();
        }
        printChildListFooter(node);
        if (useIndent) {
            println();
        }
    }

    

    protected void printIndentNodeHeader(CallTreeNode node) {
        if (useIndent) {
            printCurrIndent();
        }
        printNodeHeader(node);
        if (useIndent) {
            println();
        }
        incrIndent();
    }

    protected void printIndentNodeFooter(CallTreeNode node) {
        decrIndent();
        if (useIndent) {
            println();
            printCurrIndent();
        }
        printNodeFooter(node);
        if (useIndent) {
            println();
        }
    }

    protected void printIndentNodeValueListHeader(CallTreeNode node) {
        if (useIndent) {
            printCurrIndent();
        }
        printNodeValueListHeader(node);
        if (useIndent) {
            println();
        }
        incrIndent();
    }

    protected void printIndentNodeValueListFooter(CallTreeNode node) {
        decrIndent();
        if (useIndent) {
            printCurrIndent();
        } 
        printNodeValueListFooter(node);
        if (useIndent) {
            println();
        }
    }

    // ------------------------------------------------------------------------
    
    @Override
    public String toString() {
        return "IndentCallTreePrinter["
                + super.toString()
                + ", useIndent:" + useIndent
                + ", indentStep:" + indentStep
                + "]";
    }
    
    // ------------------------------------------------------------------------

    /**
     * Builder design-pattern
     */
    protected static abstract class Builder extends AbstractCallTreePrinter.Builder{
        
        protected boolean useIndent = true;
        protected int indentStep = 2;

        public Builder withUseIndent(boolean useIndent) {
            this.useIndent = useIndent;
            return this;
        }

        public Builder withIndentStep(int indentStep) {
            this.indentStep = indentStep;
            return this;
        }
        
    }
    
}
