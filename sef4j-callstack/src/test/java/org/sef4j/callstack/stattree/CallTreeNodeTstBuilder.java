package org.sef4j.callstack.stattree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.sef4j.callstack.stats.BasicTimeStatsLogHistogram;
import org.sef4j.callstack.stats.PendingPerfCount;
import org.sef4j.callstack.stats.PerfStats;

public class CallTreeNodeTstBuilder {

    public static CallTreeNode buildTree(List<String> paths, Map<String,Callable<?>> propFactories) {
        CallTreeNode root = CallTreeNode.newRoot();
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

    public static List<String> defaultPaths() {
        List<String> paths;
        paths = new ArrayList<String>();
        paths.add("a1/b1/c1");
        paths.add("a2/b1");
        paths.add("a3");
        return paths;
    }

    public static void fillTree(CallTreeNode root, List<String> paths, Map<String, Callable<?>> propFactories) {
        for(String pathStr : paths) {
            String[] path = pathStr.split("/");

            CallTreeNode elt = root;
            for(String pathElt : path) {
                elt = elt.getOrCreateChild(pathElt);
                
                for(Map.Entry<String,Callable<?>> propFactory : propFactories.entrySet()) {
                    elt.getOrCreateProp(propFactory.getKey(), propFactory.getValue());
                }
            }
        }
    }
    
}
