package org.sef4j.callstack.stattree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.sef4j.callstack.stats.BasicTimeStatsLogHistogram;
import org.sef4j.callstack.stats.PendingPerfCount;
import org.sef4j.callstack.stats.PerfStats;
import org.sef4j.callstack.stattree.printers.value.BasicTimeStatsLogHistogramFieldValuePrinter;
import org.sef4j.callstack.stattree.printers.value.PendingPerfCountFieldValuePrinter;
import org.sef4j.callstack.stattree.printers.value.PerfStatsFieldValuePrinter;
import org.sef4j.core.helpers.proptree.model.PropTreeNode;
import org.sef4j.core.helpers.proptree.printers.PropTreeValuePrinter;
import org.sef4j.core.helpers.proptree.printers.PropTreeValueWrapperPrinter;

public class CallTreeNodeTstBuilder {

    public static PropTreeNode buildTree(List<String> paths, Map<String,Callable<?>> propFactories) {
        PropTreeNode root = PropTreeNode.newRoot();
        if (paths == null) {
            paths = defaultPaths();
        }
        if (propFactories == null) {
            propFactories = defaultPropFactories();
        }
        fillTree(root, paths, propFactories);
        return root;
    }

    public static Map<String, Callable<?>> defaultPropFactories() {
        Map<String, Callable<?>> propFactories;
        propFactories = new HashMap<String,Callable<?>>();
        propFactories.put("histo1", BasicTimeStatsLogHistogram.FACTORY);
        propFactories.put("pending1", PendingPerfCount.FACTORY);
        propFactories.put("perfStat1", PerfStats.FACTORY);
        return propFactories;
    }


    public static Map<Class<?>, PropTreeValuePrinter<?>> defaultPerTypePrinters(boolean prefixPropName, String prefixSep, String postfixSep) {
        Map<Class<?>, PropTreeValuePrinter<?>> res = new HashMap<Class<?>, PropTreeValuePrinter<?>>();
        res.put(BasicTimeStatsLogHistogram.class, new PropTreeValueWrapperPrinter<BasicTimeStatsLogHistogram>(
                    BasicTimeStatsLogHistogramFieldValuePrinter.INSTANCE, prefixPropName, prefixSep, postfixSep));
        res.put(PendingPerfCount.class, new PropTreeValueWrapperPrinter<PendingPerfCount>(
                PendingPerfCountFieldValuePrinter.INSTANCE, prefixPropName, prefixSep, postfixSep));
        res.put(PerfStats.class, new PropTreeValueWrapperPrinter<PerfStats>(
                PerfStatsFieldValuePrinter.DEFAULT_INSTANCE, prefixPropName, prefixSep, postfixSep));
        return res;
    }
    
    
    public static List<String> defaultPaths() {
        List<String> paths;
        paths = new ArrayList<String>();
        paths.add("a1/b1/c1");
        paths.add("a2/b1");
        paths.add("a3");
        return paths;
    }

    public static void fillTree(PropTreeNode root, List<String> paths, Map<String, Callable<?>> propFactories) {
        for(String pathStr : paths) {
            String[] path = pathStr.split("/");

            PropTreeNode elt = root;
            for(String pathElt : path) {
                elt = elt.getOrCreateChild(pathElt);
                
                for(Map.Entry<String,Callable<?>> propFactory : propFactories.entrySet()) {
                    elt.getOrCreateProp(propFactory.getKey(), propFactory.getValue());
                }
            }
        }
    }

}
