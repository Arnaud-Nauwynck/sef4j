package org.sef4j.callstack.export.printer.helpers;

import java.io.PrintWriter;

import org.sef4j.callstack.stattree.CallTreeNode;

/**
 * CallTreePrinter to print as (indented) JSON, like
 * <PRE>
{
  childList: [
    a1 {
      propsMap: {
        histo1:{count0: 0, sum0: 0, count1: 0, sum1: 0, count2: 0, sum2: 0, count3: 0, sum3: 0, count4: 0, sum4: 0, count5: 0, sum5: 0, count6: 0, sum6: 0, count7: 0, sum7: 0, count8: 0, sum8: 0, count9: 0, sum9: 0},
        pending1:{pendingCount: 0, pendingSumStartTime: 0},
        perfStat1:{count0: 0, sum0: 0, count1: 0, sum1: 0, count2: 0, sum2: 0, count3: 0, sum3: 0, count4: 0, sum4: 0, count5: 0, sum5: 0, count6: 0, sum6: 0, count7: 0, sum7: 0, count8: 0, sum8: 0, count9: 0, sum9: 0},
      },
      childList: [
        b1 {
          propsMap: {
            histo1:{count0: 0, sum0: 0, count1: 0, sum1: 0, count2: 0, sum2: 0, count3: 0, sum3: 0, count4: 0, sum4: 0, count5: 0, sum5: 0, count6: 0, sum6: 0, count7: 0, sum7: 0, count8: 0, sum8: 0, count9: 0, sum9: 0},
            pending1:{pendingCount: 0, pendingSumStartTime: 0},
            perfStat1:{count0: 0, sum0: 0, count1: 0, sum1: 0, count2: 0, sum2: 0, count3: 0, sum3: 0, count4: 0, sum4: 0, count5: 0, sum5: 0, count6: 0, sum6: 0, count7: 0, sum7: 0, count8: 0, sum8: 0, count9: 0, sum9: 0},
          },
          childList: [
            c1 {
              propsMap: {
                histo1:{count0: 0, sum0: 0, count1: 0, sum1: 0, count2: 0, sum2: 0, count3: 0, sum3: 0, count4: 0, sum4: 0, count5: 0, sum5: 0, count6: 0, sum6: 0, count7: 0, sum7: 0, count8: 0, sum8: 0, count9: 0, sum9: 0},
                pending1:{pendingCount: 0, pendingSumStartTime: 0},
                perfStat1:{count0: 0, sum0: 0, count1: 0, sum1: 0, count2: 0, sum2: 0, count3: 0, sum3: 0, count4: 0, sum4: 0, count5: 0, sum5: 0, count6: 0, sum6: 0, count7: 0, sum7: 0, count8: 0, sum8: 0, count9: 0, sum9: 0},
              },

            },
          ]

        },
      ]

    },
}
</PRE>
 */
public class JsonCallTreePrinter extends AbstractIndentCallTreePrinter {

    private boolean printComment = false;
    
    // ------------------------------------------------------------------------
    
    protected JsonCallTreePrinter(Builder builder) {
        super(builder);
    }

    // ------------------------------------------------------------------------

    @Override
    public void printNodeHeader(CallTreeNode node) {
        print(node.getName());
        print(" {");
    }

    @Override
    public void printNodeFooter(CallTreeNode node) {
        print("}");
        if (node.getParent() != null) {
            print(",");
        }
        if (printComment) print("/* " + node.getName() + "*/");
    }
    
    @Override
    public void printNodeValueListHeader(CallTreeNode node) {
        print("propsMap: {");
    }

    @Override
    public void printNodeValueListFooter(CallTreeNode node) {
        print("},");
    }

    @Override
    public void printNodeValueHeader(CallTreeNode node, String propName) {
        printCurrIndent();
        // print(propName + ": "); ... cf already printed with value format printer!
    }

    @Override
    public void printNodeValueFooter(CallTreeNode node, String propName) {
        println(",");
    }

    @Override
    public void printChildListHeader(CallTreeNode node) {
        print("childList: [");
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
//        print(",");
//        print("/* ChildFooter " + node.getName() + " */");
    }
    
    // ------------------------------------------------------------------------

    public static class Builder extends AbstractIndentCallTreePrinter.Builder {
        
        public Builder(PrintWriter out) {
            super(out);
        }

        public JsonCallTreePrinter build() {
            return new JsonCallTreePrinter(this);
        }
    }
    
}
