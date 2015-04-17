package org.sef4j.testwebapp.web;

import java.io.Closeable;
import java.util.HashMap;
import java.util.Map;

import org.sef4j.callstack.CallStackElt.StackPopper;
import org.sef4j.callstack.CallStackPushPopHandler;
import org.sef4j.callstack.LocalCallStack;
import org.sef4j.callstack.handlers.CallTreeStatsUpdaterCallStackHandler;
import org.sef4j.callstack.stats.PerfStatsUtils;
import org.sef4j.callstack.stats.CumulatedBasicTimeStatsLogHistogramDTO.CumulatedBasicTimeStatsLogHistogramDTOMapper;
import org.sef4j.core.api.proptree.PropTreeNode;
import org.sef4j.core.api.proptree.PropTreeNodeDTO;
import org.sef4j.core.api.proptree.PropTreeNodeMapper;
import org.sef4j.core.api.proptree.PropTreeValueMapper;
import org.sef4j.core.api.proptree.PropTreeValuePredicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="app/rest/metricsStatsTree", produces = MediaType.APPLICATION_JSON_VALUE)
public class MetricsStatsTreeController {

	private static final Logger LOG = LoggerFactory.getLogger(MetricsStatsTreeController.class);

    private static final PropTreeNode rootWSStatsNode = PropTreeNode.newRoot();
    private static final CallTreeStatsUpdaterCallStackHandler callTreeWSStatsHandler = new CallTreeStatsUpdaterCallStackHandler(rootWSStatsNode);

    private PropTreeNodeMapper propTreeNodeDTOMapper = PerfStatsUtils.createDefaultPerfStatsDTOMapper();
    
    public MetricsStatsTreeController() {
    }
    
    @RequestMapping(value="all", method=RequestMethod.GET)
	public PropTreeNodeDTO findAll() {
        LOG.info("findAll");
	    PropTreeNodeDTO res = PropTreeNodeDTO.newRoot();
	    propTreeNodeDTOMapper.recursiveCopyToDTO(rootWSStatsNode, res, -1);
	    return res;
	}


	
	public static StatsHandlerPopper pushStats(String className, String categoryMethod, String methodName) {
	    return new StatsHandlerPopper(callTreeWSStatsHandler, className, categoryMethod, methodName);
	}
    
    public static class StatsHandlerPopper implements Closeable {
        CallStackPushPopHandler popHandler;
        StackPopper toPopCategory;
        StackPopper toPopMethod;
        
        public StatsHandlerPopper(CallStackPushPopHandler popHandler, String className, String categoryMethod, String methodName) {
            this.popHandler = popHandler;
            LocalCallStack.get().curr().addRootCallStackHandler(popHandler);
            this.toPopCategory = LocalCallStack.meth(className, categoryMethod).push();
            this.toPopMethod = LocalCallStack.meth(className, methodName).pushWithParentStartTime();
        }

        @Override
        public void close() {
            toPopMethod.close();
            toPopCategory.close();
            LocalCallStack.get().curr().removeRootCallStackHandler(popHandler);
        }
    }
}
