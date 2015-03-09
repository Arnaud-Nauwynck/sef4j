package org.sef4j.callstack.export.printer.helpers;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import org.junit.Before;
import org.junit.Test;
import org.sef4j.callstack.stattree.CallTreeNode;
import org.sef4j.callstack.stattree.CallTreeNodeTstBuilder;

public class TextCallTreePrinterTest {

    protected ByteArrayOutputStream buffer;
    protected PrintWriter out;
    protected TextCallTreePrinter sut;
    
    @Before
    public void setup() {
        this.buffer = new ByteArrayOutputStream();
        this.out = new PrintWriter(buffer);
        TextCallTreePrinter.Builder builder = new TextCallTreePrinter.Builder(out);
        builder.withPropPerTypePrinter(CallTreeNodeTstBuilder.defaultPerTypePrinters(true, ":{", "}")); // upcast to abstract builder!.. cannot chain method
        this.sut = builder.build();
    }
    
    @Test
    public void testRecursivePrintNodes() {
        // Prepare
        CallTreeNode root = CallTreeNodeTstBuilder.buildTree(null, null);
        // Perform
        sut.recursivePrintNodes(root, -1);
        // Post-check
        out.flush();
        String res = buffer.toString();
        System.out.println(res);
    }
}
