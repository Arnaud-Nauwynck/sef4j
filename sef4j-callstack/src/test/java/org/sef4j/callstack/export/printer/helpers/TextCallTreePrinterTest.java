package org.sef4j.callstack.export.printer.helpers;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import org.junit.Test;
import org.sef4j.callstack.stattree.CallTreeNode;
import org.sef4j.callstack.stattree.CallTreeNodeTstBuilder;

public class TextCallTreePrinterTest {

    protected ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    protected PrintWriter out = new PrintWriter(buffer);
    protected TextCallTreePrinter sut = new TextCallTreePrinter.Builder(out).build();
    
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
