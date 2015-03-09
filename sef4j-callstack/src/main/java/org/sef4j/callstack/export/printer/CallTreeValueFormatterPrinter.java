package org.sef4j.callstack.export.printer;

import java.text.Format;

import org.sef4j.callstack.export.valueformats.CallTreeValueFormatter;
import org.sef4j.callstack.export.valueformats.FormatCallTreeValueFormatter;
import org.sef4j.callstack.stattree.CallTreeNode;

/**
 * CallTreeValuePrinter adapter to use CallTreeValueFormatter
 * 
 * @param <T>
 */
public class CallTreeValueFormatterPrinter<T> implements CallTreeValuePrinter<T> {

    private final CallTreeValueFormatter<T> formatter;

    // ------------------------------------------------------------------------

    public CallTreeValueFormatterPrinter(CallTreeValueFormatter<T> formatter) {
        this.formatter = formatter;
    }

    /** helper constructor */
    public CallTreeValueFormatterPrinter(Format delegate, boolean prefixPropName, String prefixSep, String postfixSep) {
        this(new FormatCallTreeValueFormatter<T>(delegate, prefixPropName, prefixSep, postfixSep));
    }
    
    // ------------------------------------------------------------------------

    @Override
    public void printValue(CallTreePrinter output, CallTreeNode node, String propName, T propValue) {
        String text = formatter.formatValue(node, propName, propValue);
        output.out.append(text);
    }
    
}