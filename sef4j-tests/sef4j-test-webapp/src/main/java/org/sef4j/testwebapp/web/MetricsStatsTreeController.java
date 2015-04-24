package org.sef4j.testwebapp.web;

import java.io.Closeable;
import java.util.concurrent.TimeUnit;

import org.sef4j.callstack.CallStackElt.StackPopper;
import org.sef4j.callstack.CallStackPushPopHandler;
import org.sef4j.callstack.LocalCallStack;
import org.sef4j.callstack.handlers.CallTreeStatsUpdaterCallStackHandler;
import org.sef4j.callstack.stats.ThreadTimeUtils;
import org.sef4j.callstack.stats.helpers.PerfStatsDTOMapperUtils;
import org.sef4j.core.api.proptree.PropTreeNode;
import org.sef4j.core.api.proptree.PropTreeNodeDTO;
import org.sef4j.core.api.proptree.PropTreeNodeDTOMapper;
import org.sef4j.core.helpers.AsyncUtils;
import org.sef4j.core.helpers.PeriodicTask;
import org.sef4j.testwebapp.service.AtmospherePerfStatsPublishService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="app/rest/metricsStatsTree", produces = MediaType.APPLICATION_JSON_VALUE)
public class MetricsStatsTreeController {

	private static final Logger LOG = LoggerFactory.getLogger(MetricsStatsTreeController.class);

    private static final PropTreeNode rootWSStatsNode = PropTreeNode.newRoot();

    private PropTreeNodeDTOMapper defaultPropTreeNodeDTOMapper = 
    		PerfStatsDTOMapperUtils.createDTOMapper();
    
    private PropTreeNodeDTOMapper pendingPropTreeNodeDTOMapper = 
    		PerfStatsDTOMapperUtils.createPendingCountFilterDTOMapper(0);

    private PeriodicTask publisherPeriodicTask = new PeriodicTask("pendingDiffPublisherPeriodicTask", 
    		() -> pendingDiffPublisher(),
    		10, TimeUnit.SECONDS, AsyncUtils.defaultScheduledThreadPool());
    
	@Autowired 
	private AtmospherePerfStatsPublishService statsPublishService;

	
	
    // ------------------------------------------------------------------------
    
    public MetricsStatsTreeController() {
    }
    
    @RequestMapping(value="stats", method=RequestMethod.GET)
	public PropTreeNodeDTO findAll() {
        LOG.info("findAll");
	    PropTreeNodeDTO res = defaultPropTreeNodeDTOMapper.map(rootWSStatsNode);
	    return res;
	}

    @RequestMapping(value="statsFilterByMin", method=RequestMethod.GET)
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

	    // LOG.info("findAllPending => \n" + PropTreeNodeDTOPrinter.recursiveDumpPendingCount(res));
	    return res;
	}

    @RequestMapping(value="startAtmospherePeriodicTaskPublisher", method=RequestMethod.POST)
	public void startAtmospherePeriodicTaskPublisher() {
    	LOG.info("starAtmospherePeriodicTaskPublisher");
    	if (! publisherPeriodicTask.isStarted()) {
    		publisherPeriodicTask.start();
    	}
    }
    
    @RequestMapping(value="stopAtmospherePeriodicTaskPublisher", method=RequestMethod.POST)
	public void stopAtmospherePeriodicTaskPublisher() {
    	LOG.info("stopAtmospherePeriodicTaskPublisher");
    	if (publisherPeriodicTask.isStarted()) {
    		publisherPeriodicTask.stop();
    	}
    }

    @RequestMapping(value="setPeriodPublisher", method=RequestMethod.POST)
	public void setPeriod(int period) {
    	publisherPeriodicTask.setPeriod(period, TimeUnit.SECONDS);
    }
    
    protected void pendingDiffPublisher() {
	    // TODO ... use incremental publisher (collect diff PendingCount only !)
    	PropTreeNodeDTO pendingTreeNode = pendingPropTreeNodeDTOMapper.map(rootWSStatsNode);
    	LOG.info("publish stats");
    	statsPublishService.publish(pendingTreeNode);
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
