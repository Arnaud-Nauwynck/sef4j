package org.sef4j.callstack.stattree.printers;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.sef4j.callstack.stattree.CallTreeNodeTstBuilder;
import org.sef4j.callstack.stattree.printers.PathJsonCallTreePrinter;
import org.sef4j.core.helpers.proptree.model.PropTreeNode;

public class PathJsonCallTreePrinterTest {

    protected StringWriter buffer;
    protected PrintWriter out;
    protected PathJsonCallTreePrinter sut;

    
    @Before
    public void setup() {
        buffer = new StringWriter();
        out = new PrintWriter(buffer);
        PathJsonCallTreePrinter.Builder builder = new PathJsonCallTreePrinter.Builder();
        builder.withPropPerTypePrinter(CallTreeNodeTstBuilder.defaultPerTypePrinters(false, "{", "}")); // upcast to abstract builder!.. cannot chain method
        this.sut = builder.build(out);
    }
    
    @Test
    public void testRecursivePrintNodes() throws IOException {
        // Prepare
        PropTreeNode root = CallTreeNodeTstBuilder.buildTree(null, null);
        // Perform
        sut.recursivePrintNodes(root, -1);
        // Post-check
        out.flush();
        String res = buffer.toString();
        System.out.println(res);
        
        InputStream expectedStream = getClass().getResourceAsStream("PathJsonCallTreePrintTest-testRecursivePrintNodes.txt");
        String expectedRes = IOUtils.toString(expectedStream);
        Assert.assertEquals(expectedRes, res);
    }
}
