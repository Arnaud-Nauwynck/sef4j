package org.sef4j.testwebapp.service;

import java.io.Closeable;

import org.sef4j.callstack.CallStackElt.StackPopper;
import org.sef4j.callstack.CallStackPushPopHandler;
import org.sef4j.callstack.LocalCallStack;
import org.sef4j.callstack.handlers.CallTreeStatsUpdaterCallStackHandler;
import org.sef4j.callstack.stats.ThreadTimeUtils;
import org.sef4j.callstack.stats.helpers.PerfStatsDTOMapperUtils;
import org.sef4j.callstack.stats.helpers.PropTreeNodeDTOPrinter;
import org.sef4j.core.helpers.proptree.dto.PropTreeNodeDTO;
import org.sef4j.core.helpers.proptree.model.PropTreeNode;
import org.sef4j.core.helpers.proptree.model.PropTreeNodeDTOMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Component
public class MetricsStatsTreeRegistry {

	private static final Logger LOG = LoggerFactory.getLogger(MetricsStatsTreeRegistry.class);

    private static final PropTreeNode rootWSStatsNode = PropTreeNode.newRoot();

    public static PropTreeNode getRootWSStatsNode() {
    	return rootWSStatsNode;
    }

    private PropTreeNodeDTOMapper defaultPropTreeNodeDTOMapper = 
    		PerfStatsDTOMapperUtils.createDTOMapper();
    
    private PropTreeNodeDTOMapper pendingPropTreeNodeDTOMapper = 
    		PerfStatsDTOMapperUtils.createPendingCountFilterDTOMapper(0);

    // ------------------------------------------------------------------------
    
    public MetricsStatsTreeRegistry() {
    }
    
	public PropTreeNodeDTO findAll() {
        LOG.info("findAll");
	    PropTreeNodeDTO res = defaultPropTreeNodeDTOMapper.map(rootWSStatsNode);
	    return res;
	}

	public PropTreeNodeDTO findFilterByMin(
			int filterMinPendingCount, int filterMinCount, 
			long filterMinSumElapsed, long filterMinSumThreadUserTime, long filterMinSumThreadCpuTime
			) {
        LOG.info("findFilterByMin");
	    PropTreeNodeDTOMapper mapper = PerfStatsDTOMapperUtils.createDTOMapper(
	    		filterMinPendingCount, filterMinCount, 
	    		filterMinSumElapsed, filterMinSumThreadUserTime, filterMinSumThreadCpuTime);
	    PropTreeNodeDTO res = mapper.map(rootWSStatsNode);
	    return res;
	}

    @RequestMapping(value="pendingCount", method=RequestMethod.GET)
	public PropTreeNodeDTO findAllPending() {
	    PropTreeNodeDTO res = pendingPropTreeNodeDTOMapper.map(rootWSStatsNode);
	    
	    long clockNanos = ThreadTimeUtils.getTime();
		long clockMillis = ThreadTimeUtils.nanosToMillis(clockNanos);
	    		// System.currentTimeMillis();
		res.putProp("clockNanos", clockNanos);
	    res.putProp("clockMillis", clockMillis);
	    
	    long timeNowMillis = System.currentTimeMillis();
	    res.putProp("timeNowMillis", timeNowMillis);

	    LOG.debug("findAllPending => \n" + PropTreeNodeDTOPrinter.recursiveDumpPendingCount(res));
	    return res;
	}

	
    public PropTreeNode getPropTreeNodeByPathOrNull(String[] path) {
    	return rootWSStatsNode.getChildByPathOrNull(path);
    }
    
    // ------------------------------------------------------------------------
    
	public static StatsHandlerPopper pushTopLevelStats(String className, String categoryMethod, String methodName) {
	    CallTreeStatsUpdaterCallStackHandler threadCallTreeWSStatsHandler = new CallTreeStatsUpdaterCallStackHandler(rootWSStatsNode);
		return new StatsHandlerPopper(threadCallTreeWSStatsHandler, className, categoryMethod, methodName);
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
