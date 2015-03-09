package org.sef4j.callstack.export.printer.helpers;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Map;

import org.sef4j.callstack.export.printer.CallTreePrinter;
import org.sef4j.callstack.export.printer.CallTreeValuePrinter;
import org.sef4j.callstack.stattree.CallTreeNode;

/**
 * 
 */
public abstract class AbstractIndentCallTreePrinter extends CallTreePrinter {

    protected Map<String,CallTreeValuePrinter<?>> propPerNamePrinter;
    protected Map<Class<?>,CallTreeValuePrinter<?>> propPerTypePrinter;
    protected CallTreeValuePrinter<?> propDefaultPrinter;
    
    protected boolean useIndent = true;
    protected int indentStep = 2;
    protected int currIndentLevel;
    
    // ------------------------------------------------------------------------

    protected AbstractIndentCallTreePrinter(Builder builder) {
        super(builder.out);
        this.propPerNamePrinter = builder.propPerNamePrinter;
        this.propPerTypePrinter = builder.propPerTypePrinter;
        this.propDefaultPrinter = builder.propDefaultPrinter;
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
                    valuePrinter.printValue(this, node, propName, propValue);
                    printNodeValueFooter(node, propName);
                }
            }

            printIndentNodeValueListFooter(node);
        }
    }

    @SuppressWarnings("unchecked")
    private CallTreeValuePrinter<Object> resolvePropValuePrinter(String propName, Object propValue) {
        CallTreeValuePrinter<?> valuePrinter = null;
        if (propPerNamePrinter != null) {
            valuePrinter = propPerNamePrinter.get(propName);
        }
        if (valuePrinter == null 
                && propPerTypePrinter != null && !propPerTypePrinter.isEmpty()) {
            // search by type
            Class<?> propClss = propValue.getClass();
            valuePrinter = propPerTypePrinter.get(propClss);
            // when not found, find by parent class type... until Object.class
            while(valuePrinter == null && propClss != Object.class) {
                propClss = propClss.getSuperclass();
                valuePrinter = propPerTypePrinter.get(propClss);
            }
        }
        if (valuePrinter == null 
                && propDefaultPrinter != null) {
            propDefaultPrinter = valuePrinter;
        }
        return (CallTreeValuePrinter<Object>) valuePrinter;
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
        return "CallTreePrinter["
                + ((propPerNamePrinter != null)? "propNames:" + propPerNamePrinter.keySet() + ", " : "")
                + ((propPerTypePrinter != null)? "propTypes:" + propPerTypePrinter.keySet() + ", " : "")
                + ((propDefaultPrinter != null)? "propDefault, " : "")
                + "]";
    }
    
    // ------------------------------------------------------------------------

    /**
     * Builder design-pattern
     */
    protected static abstract class Builder {

        protected PrintWriter out;    
        protected Map<String,CallTreeValuePrinter<?>> propPerNamePrinter;
        protected Map<Class<?>,CallTreeValuePrinter<?>> propPerTypePrinter;
        protected CallTreeValuePrinter<?> propDefaultPrinter;

        
        protected Builder(PrintWriter out) {
            this.out = out;
        }

        public Builder withPropPerNamePrinter(Map<String, CallTreeValuePrinter<?>> propPerNamePrinter) {
            this.propPerNamePrinter = propPerNamePrinter;
            return this;
        }

        public Builder withPropPerTypePrinter(Map<Class<?>, CallTreeValuePrinter<?>> propPerTypePrinter) {
            this.propPerTypePrinter = propPerTypePrinter;
            return this;
        }

        public Builder withPropDefaultPrinter(CallTreeValuePrinter<?> propDefaultPrinter) {
            this.propDefaultPrinter = propDefaultPrinter;
            return this;
        }
        
    }
    
}
