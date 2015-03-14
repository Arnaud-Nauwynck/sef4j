package org.sef4j.core.helpers.exporters;

import java.io.PrintWriter;

public interface ValuePrinter<T> {
    
    public void printValue(PrintWriter output, T propValue); 

}
