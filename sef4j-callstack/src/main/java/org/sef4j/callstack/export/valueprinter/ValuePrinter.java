package org.sef4j.callstack.export.valueprinter;

import java.io.PrintWriter;

public interface ValuePrinter<T> {
    
    public void printValue(PrintWriter output, T propValue); 

}
