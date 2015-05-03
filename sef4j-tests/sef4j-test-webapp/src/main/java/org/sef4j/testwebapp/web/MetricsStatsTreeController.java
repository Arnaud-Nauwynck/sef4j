package org.sef4j.testwebapp.web;

import java.util.concurrent.TimeUnit;

import org.sef4j.core.api.proptree.PropTreeNodeDTO;
import org.sef4j.core.helpers.AsyncUtils;
import org.sef4j.core.helpers.PeriodicTask;
import org.sef4j.testwebapp.service.MetricsStatsTreeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="app/rest/metricsStatsTree", produces = MediaType.APPLICATION_JSON_VALUE)
public class MetricsStatsTreeController {

	private static final String TOPIC_PENDING_COUNT = "/topic/pendingCount";

    private static final Logger LOG = LoggerFactory.getLogger(MetricsStatsTreeController.class);

	@Autowired 
	protected MetricsStatsTreeRegistry metricsStatsTreeRegistry;

	@Autowired
    private SimpMessagingTemplate simpMessageSender;
    
	protected PeriodicTask pendingCountPublisherPeriodicTask = new PeriodicTask("pendingCountPublisherPeriodicTask",
	        () -> pendingCountPublisherPeriodicTask(), 
	        15, TimeUnit.SECONDS, AsyncUtils.defaultScheduledThreadPool()); 
	
    // ------------------------------------------------------------------------
    
    public MetricsStatsTreeController() {
    }
    
    @RequestMapping(value="stats", method=RequestMethod.GET)
	public PropTreeNodeDTO findAll() {
        LOG.info("findAll");
	    PropTreeNodeDTO res = metricsStatsTreeRegistry.findAll();
	    return res;
	}

    @RequestMapping(value="statsFilterByMin", method=RequestMethod.GET)
	public PropTreeNodeDTO findFilterByMin(
			int filterMinPendingCount, int filterMinCount, 
			long filterMinSumElapsed, long filterMinSumThreadUserTime, long filterMinSumThreadCpuTime
			) {
        LOG.info("findFilterByMin");
	    PropTreeNodeDTO res = metricsStatsTreeRegistry.findFilterByMin(filterMinPendingCount, filterMinCount, filterMinSumElapsed, filterMinSumThreadUserTime, filterMinSumThreadCpuTime);
	    return res;
	}

    @RequestMapping(value="pendingCount", method=RequestMethod.GET)
	public PropTreeNodeDTO findAllPending() {
	    PropTreeNodeDTO res = metricsStatsTreeRegistry.findAllPending();
	    return res;
	}

    
    @RequestMapping(value="startPendingCountPublisherPeriodicTask", method=RequestMethod.POST)
    public void startPendingCountPublisherPeriodicTask() {
        LOG.info("startPendingCountPublisherPeriodicTask");
        pendingCountPublisherPeriodicTask.start();
    }

    @RequestMapping(value="stopPendingCountPublisherPeriodicTask", method=RequestMethod.POST)
    public void stopPendingCountPublisherPeriodicTask() {
        LOG.info("stopPendingCountPublisherPeriodicTask");
        pendingCountPublisherPeriodicTask.stop();
    }
    
    public void pendingCountPublisherPeriodicTask() {
        PropTreeNodeDTO res = metricsStatsTreeRegistry.findAllPending();
        if (res.getChildMap().isEmpty()) {
            LOG.info("pendingCountPublisherPeriodicTask ... skip sending");
            return;
        }
        LOG.info("pendingCountPublisherPeriodicTask ... sending message to " + TOPIC_PENDING_COUNT);
        simpMessageSender.convertAndSend(TOPIC_PENDING_COUNT, res);
    }
    
}
