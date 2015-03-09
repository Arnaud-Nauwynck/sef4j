package org.sef4j.callstack.export.valueformats;

import java.text.Format;

import org.sef4j.callstack.stattree.CallTreeNode;

/**
 * adapter implements CallTreeValueFormatter, delegate to java.text.Format.
 *  (CallTree,name,Object) -> String text 
 *
 * using flags, you can format to "<<value>>" or "<<propName>> : { <<value>> }", 
 * or more generally:  "<<propName>> <<prefixSep>> <<value>> <<postfixSep>>"
 *  
 * @param <T>
 */
public class FormatCallTreeValueFormatter<T> implements CallTreeValueFormatter<T> {
    
    private final Format delegate;
    private final boolean prefixPropName;
    private final String prefixSep;
    private final String postfixSep;
    
    public FormatCallTreeValueFormatter(Format delegate, boolean prefixPropName, String prefixSep, String postfixSep) {
        super();
        this.delegate = delegate;
        this.prefixPropName = prefixPropName;
        this.prefixSep = prefixSep;
        this.postfixSep = postfixSep;
    }

    public String formatValue(CallTreeNode node, String propName, T propValue) {
        StringBuffer sb = new StringBuffer();
        if (prefixPropName) {
            sb.append(propName);
        }
        if (prefixSep != null) {
            sb.append(prefixSep);
        }
        delegate.format((Object) propValue, sb, null);
        if (postfixSep != null) {
            sb.append(postfixSep);
        }
        return sb.toString();
    }
    
}
