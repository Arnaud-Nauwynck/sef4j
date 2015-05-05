package org.sef4j.testwebapp.service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.sef4j.core.api.proptree.PropTreeNodeDTO;
import org.sef4j.core.helpers.PeriodicTask;
import org.sef4j.core.util.AsyncUtils;
import org.sef4j.testwebapp.dto.SubscriptionCommandDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class MetricsStatsPublisher {

	private static final Logger LOG = LoggerFactory.getLogger(MetricsStatsPublisher.class);

	private static final String TOPIC_PENDING_COUNT = "/topic/pendingCount";

	@Autowired 
	protected MetricsStatsTreeRegistry metricsStatsTreeRegistry;

	@Autowired
    private SimpMessagingTemplate simpMessageSender;
    
	protected PeriodicTask pendingCountBroadcastPublisherPeriodicTask = new PeriodicTask("pendingCountPublisherPeriodicTask",
	        () -> pendingCountBroadcastPublisherPeriodicTask(), 
	        15, TimeUnit.SECONDS, AsyncUtils.defaultScheduledThreadPool()); 

	@Autowired 
	protected PerfStatsSubscriptionSessionManager subscriptionMgr;

	protected PeriodicTask sessionSubscribedPathPublisherPeriodicTask = new PeriodicTask("sessionSubscribedPathPublisherPeriodicTask",
	        () -> sessionSubscribedPathPublisherPeriodicTask(), 
	        15, TimeUnit.SECONDS, AsyncUtils.defaultScheduledThreadPool()); 

	
	// ------------------------------------------------------------------------

	public MetricsStatsPublisher() {
	}

	// ------------------------------------------------------------------------

	
    public void startPendingCountPublisherPeriodicTask() {
        LOG.info("startPendingCountPublisherPeriodicTask");
        pendingCountBroadcastPublisherPeriodicTask.start();
    }
 
    public void stopPendingCountPublisherPeriodicTask() {
	   LOG.info("stopPendingCountPublisherPeriodicTask");
	   pendingCountBroadcastPublisherPeriodicTask.stop();
    }
    
    public void pendingCountBroadcastPublisherPeriodicTask() {
        PropTreeNodeDTO res = metricsStatsTreeRegistry.findAllPending();
        if (res.getChildMap().isEmpty()) {
            LOG.info("pendingCountPublisherPeriodicTask ... skip sending");
            return;
        }
        LOG.info("pendingCountPublisherPeriodicTask ... sending message to " + TOPIC_PENDING_COUNT);
        simpMessageSender.convertAndSend(TOPIC_PENDING_COUNT, res);
    }
    
    public List<String> asyncSubscriptionCommandsRequest(WebSocketSession webSocketSession, List<SubscriptionCommandDTO> subscriptionCommands) {
    	LOG.info("asyncSubscriptionCommandsRequest from webSocketSession:" + webSocketSession.getId());
    	List<String> res = subscriptionMgr.handleSubscriptionCommandsRequest(webSocketSession, subscriptionCommands);
    	return res;
    }

    public void startSessionSubscribedPathPublisherPeriodicTask() {
        LOG.info("startSessionSubscribedPathPublisherPeriodicTask");
        sessionSubscribedPathPublisherPeriodicTask.start();
    }
 
    public void stopSessionSubscribedPathPublisherPeriodicTask() {
	   LOG.info("stopSessionSubscribedPathPublisherPeriodicTask");
	   sessionSubscribedPathPublisherPeriodicTask.stop();
    }

    public void sessionSubscribedPathPublisherPeriodicTask() {
    	subscriptionMgr.publishEventsForSubscriptions();
    }
    
}
