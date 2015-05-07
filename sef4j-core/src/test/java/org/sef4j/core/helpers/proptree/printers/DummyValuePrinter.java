package org.sef4j.core.helpers.proptree.printers;

import java.io.PrintWriter;
import java.util.List;

import org.sef4j.core.helpers.proptree.DummyCount;

/**
 * ValuePrinter for DummyCount
 * <BR/>
 * print as  
 * <PRE>count1: 123, count2: 456</PRE>
 */
public class DummyValuePrinter implements ValuePrinter<DummyCount>  {

    public static final DummyValuePrinter INSTANCE = new DummyValuePrinter();
    
    // ------------------------------------------------------------------------

    public DummyValuePrinter() {
    }
    
    // ------------------------------------------------------------------------

    public void printValues(PrintWriter output, String name, List<DummyCount> values) {
        for(DummyCount value : values) {
            printValue(output, name, value);
        }
    }
    
    @Override
    public void printValue(PrintWriter output, String name, DummyCount value) {
        output.print("count1: ");
        output.print(value.getCount1());
        output.print(", count2: ");
        output.print(value.getCount2());
    }
    
}
