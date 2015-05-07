package org.sef4j.core.helpers.proptree.printers;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.Assert;
import org.junit.Test;
import org.sef4j.core.helpers.proptree.DummyCount;
import org.sef4j.core.helpers.proptree.model.PropTreeNode;

public class PropTreeValueWrapperPrinterTest {

    @Test
    public void testFormatValue() {
        // Prepare
        StringWriter buffer = new StringWriter();
        PrintWriter out = new PrintWriter(buffer);
        DummyValuePrinter underlyingFmt = new DummyValuePrinter();
        DummyCount value = new DummyCount();        
        PropTreeValueWrapperPrinter<DummyCount> sut = new PropTreeValueWrapperPrinter<DummyCount>(underlyingFmt, false, null, null);
        // Perform
        sut.printValue(out, PropTreeNode.newRoot(), "testProp", value);
        // Post-check
        out.flush();
        Assert.assertEquals("count1: 0, count2: 0", buffer.toString());
    }

    @Test
    public void testFormatValue_wrap() {
        // Prepare
        StringWriter buffer = new StringWriter();
        PrintWriter out = new PrintWriter(buffer);
        DummyValuePrinter underlyingFmt = new DummyValuePrinter();
        DummyCount value = new DummyCount();        
        PropTreeValueWrapperPrinter<DummyCount> sut = new PropTreeValueWrapperPrinter<DummyCount>(underlyingFmt, true, ": {", "}");
        // Perform
        sut.printValue(out, PropTreeNode.newRoot(), "testProp", value);
        // Post-check
        out.flush();
        Assert.assertEquals("testProp: {count1: 0, count2: 0}", buffer.toString());
    }
    
}
