package org.sef4j.core.helpers.proptree.printers;

import java.io.PrintWriter;
import java.util.List;

public interface ValuePrinter<T> {
    
    public void printValues(PrintWriter output, String name, List<T> propValues);
    
    public void printValue(PrintWriter output, String name, T propValue); 

}
