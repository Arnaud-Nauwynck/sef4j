package org.sef4j.testwebapp.web;

import java.util.List;

import org.sef4j.core.api.session.SubscriptionCommandDTO;
import org.sef4j.core.api.session.SubscriptionResponseDTO;
import org.sef4j.core.helpers.proptree.dto.PropTreeNodeDTO;
import org.sef4j.springmsg.websocket.ClientSessionTransportWebSocketHandler;
import org.sef4j.testwebapp.service.MetricsStatsPublisher;
import org.sef4j.testwebapp.service.MetricsStatsTreeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.WebSocketSession;

@RestController
@RequestMapping(value="app/rest/metricsStatsTree", produces = MediaType.APPLICATION_JSON_VALUE)
public class MetricsStatsTreeController {

    private static final Logger LOG = LoggerFactory.getLogger(MetricsStatsTreeController.class);

	@Autowired 
	protected MetricsStatsTreeRegistry metricsStatsTreeRegistry;

	@Autowired 
	protected MetricsStatsPublisher metricsStatsPublisher;
	
	@Autowired 
	protected ClientSessionTransportWebSocketHandler clientSessionWSHandler;
	
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
        metricsStatsPublisher.startPendingCountPublisherPeriodicTask();
    }

    @RequestMapping(value="stopPendingCountPublisherPeriodicTask", method=RequestMethod.POST)
    public void stopPendingCountPublisherPeriodicTask() {
        metricsStatsPublisher.stopPendingCountPublisherPeriodicTask();
    }
    
    @MessageMapping(value="/pendingCount/subscription")
    @SendToUser(broadcast=false)
    public List<SubscriptionResponseDTO> handleSubscriptionCommands(WebSocketSession webSocketSession, List<SubscriptionCommandDTO> subscriptionCommands) {
    	List<SubscriptionResponseDTO> res = clientSessionWSHandler.handleSubscriptionCommands(webSocketSession, subscriptionCommands);
    	return res;
    }
    
}
 