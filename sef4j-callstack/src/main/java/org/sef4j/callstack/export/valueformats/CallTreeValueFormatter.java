package org.sef4j.callstack.export.valueformats;

import org.sef4j.callstack.stattree.CallTreeNode;

/**
 * Function interface for formatting value object from a CallTreeNode 
 *  (CallTree,name,Object) -> String text 
 * 
 * @param <T>
 */
public interface CallTreeValueFormatter<T> {
    
    public abstract String formatValue(CallTreeNode node, String propName, T propValue);
    
}
