package org.sef4j.testwebapp.service;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sef4j.callstack.stats.helpers.PerfStatsDTOMapperUtils;
import org.sef4j.core.helpers.proptree.dto.PropTreeNodeDTO;
import org.sef4j.core.helpers.proptree.model.PropTreeNode;
import org.sef4j.core.helpers.proptree.model.PropTreeNodeDTOMapper;
import org.sef4j.testwebapp.dto.SubscriptionCommandDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * class to manage PerfStats path subscriptions per WebSocketSession
 * 
 * for sending events per subscriptions
 */
@Component
public class PerfStatsSubscriptionSessionManager {

	private static final Logger LOG = LoggerFactory.getLogger(PerfStatsSubscriptionSessionManager.class);
	
	private static class SubscriptionEntry {
		private WebSocketSubscriptions owner;
		private final String subscriptionPath;
		private int count;
		private PropTreeNodeDTOMapper dtoMapper;
		private Object lastPublishedValue;
		
		public SubscriptionEntry(WebSocketSubscriptions owner, String subscriptionPath, PropTreeNodeDTOMapper dtoMapper) {
			this.owner = owner;
			this.subscriptionPath = subscriptionPath;
			this.dtoMapper = dtoMapper;
		}

	}
	
	private static class WebSocketSubscriptions {
		private WebSocketSession webSocketSession;
		Map<String,SubscriptionEntry> subscriptions = new HashMap<String,SubscriptionEntry>();
		public int sendFailedCount;

		public WebSocketSubscriptions(WebSocketSession webSocketSession) {
			this.webSocketSession = webSocketSession;
		}
		
		
	}
	
	
	private static class PathSubscriptions {
		String path;
		SubscriptionEntry[] subscriptionEntries = new SubscriptionEntry[0]; // copy on write
		WeakReference<PropTreeNode> pathRef;
		
		public PathSubscriptions(String path) {
			this.path = path;
		}
		
	}
	
	private Object lockSessions = new Object();
	private Map<String,WebSocketSubscriptions> sessions2subscriptions = new HashMap<String,WebSocketSubscriptions>();
	
	private Object lockSubscriptions = new Object();
	private Map<String,PathSubscriptions> path2subscriptionEntries = new HashMap<String,PathSubscriptions>();
	
	@Autowired 
	protected MetricsStatsTreeRegistry metricsStatsTreeRegistry;
	
//	@Autowired 
//	protected CompositeMessageConverter compositeMessageConverter;
	// can inject from spring??
	protected ObjectMapper jsonConverter = new ObjectMapper();
	

	// ------------------------------------------------------------------------

	public PerfStatsSubscriptionSessionManager() {
	}

	// ------------------------------------------------------------------------

	/** called from WebSocketHandler */
	public void onWebSocketSessionCreated(WebSocketSession session) {
		String id = session.getId();
		LOG.info("onWebSocketSessionCreated " + id);
		synchronized (lockSessions) {
			WebSocketSubscriptions entry = new WebSocketSubscriptions(session);
			sessions2subscriptions.put(id, entry);
		}
	}

	/** called from WebSocketHandler */
	public void onWebSocketSessionDeleted(WebSocketSession session) {
		String id = session.getId();
		LOG.info("onWebSocketSessionDeleted " + id);
		synchronized (lockSessions) {
			sessions2subscriptions.remove(id);
		}
	}

	protected WebSocketSubscriptions getWebSocketSessionSubscriptions(WebSocketSession session) {
		String id = session.getId();
		synchronized (lockSessions) {
			return sessions2subscriptions.remove(id);
		}
	}
	
	public void addSubscription(WebSocketSession session, String subscriptionPath, PropTreeNodeDTOMapper dtoMapper) {
		String subscrPathPropId = subscriptionPath + ":" + dtoMapper.toString();
		WebSocketSubscriptions subscrs = getWebSocketSessionSubscriptions(session);
		synchronized(lockSubscriptions) {
			SubscriptionEntry subscriptionEntry = subscrs.subscriptions.get(subscrPathPropId);
			if (subscriptionEntry == null) {
				
				subscriptionEntry = new SubscriptionEntry(subscrs, subscriptionPath, dtoMapper);
				subscrs.subscriptions.put(subscrPathPropId, subscriptionEntry);
			}
			subscriptionEntry.count++;
			
			// also add in map path->subscription
			if (subscriptionEntry.count == 1) {
				PathSubscriptions pathSubscriptions = getOrCreateSubscriptionsPerPath(subscriptionPath);
				
				List<SubscriptionEntry> subscEntries = new ArrayList<>(Arrays.asList(pathSubscriptions.subscriptionEntries)); 
				subscEntries.add(subscriptionEntry);
				pathSubscriptions.subscriptionEntries = subscEntries.toArray(new SubscriptionEntry[subscEntries.size()]);
			}
		}
	}

	public void removeSubscription(WebSocketSession session, String subscriptionPath, PropTreeNodeDTOMapper dtoMapper) {
		String subscrPathPropId = subscriptionPath + ":" + dtoMapper.toString();
		WebSocketSubscriptions subscrs = getWebSocketSessionSubscriptions(session);
		synchronized(lockSubscriptions) {
			SubscriptionEntry subscriptionEntry = subscrs.subscriptions.get(subscrPathPropId);
			if (subscriptionEntry == null) {
				LOG.info("subscription not found to remove => ignore, do nothing");
				return;
			}
			subscriptionEntry.count--;
			if (subscriptionEntry.count == 0) {
				subscrs.subscriptions.remove(subscriptionPath);
			}

			// also remove in map path->subscription
			// also add in map path->subscription
			if (subscriptionEntry.count == 0) {
				PathSubscriptions pathSubscriptions = getOrCreateSubscriptionsPerPath(subscriptionPath);
				
				List<SubscriptionEntry> subscEntries = new ArrayList<>(Arrays.asList(pathSubscriptions.subscriptionEntries)); 
				subscEntries.remove(subscriptionEntry);
				pathSubscriptions.subscriptionEntries = subscEntries.toArray(new SubscriptionEntry[subscEntries.size()]);

				if (pathSubscriptions.subscriptionEntries.length == 0) {
					path2subscriptionEntries.remove(subscriptionPath);
				}
			}
		}
	}

	public List<String> handleSubscriptionCommandsRequest(WebSocketSession webSocketSession, List<SubscriptionCommandDTO> subscriptionCommands) {
		List<String> res = new ArrayList<String>();
    	LOG.info("handleSubscriptionCommandsRequest for webSocketSession:" + webSocketSession.getId());
		for(SubscriptionCommandDTO subscriptionCommand : subscriptionCommands) {
			String cmd = subscriptionCommand.getCommand();
			String path = subscriptionCommand.getPath();
			String propMapperName = subscriptionCommand.getPropMapper();
			PropTreeNodeDTOMapper propDtoMapper = propMapperByName(propMapperName);
			
			if ("ADD".equalsIgnoreCase(cmd)) {
				addSubscription(webSocketSession, path, propDtoMapper);
				res.add("OK");
			} else if ("REMOVE".equalsIgnoreCase(cmd)) {
				removeSubscription(webSocketSession, path, propDtoMapper);
				res.add("OK");
			} else {
				LOG.warn("unrecognised subscriptionCommand: " + cmd + " .. ignore");
				res.add("ERROR: unrecognised command " + cmd);
			}
		}
		return res;
	}

	private PropTreeNodeDTOMapper propMapperByName(String propMapperName) {
		PropTreeNodeDTOMapper res = (propMapperName.equals("PendingPerfCount"))? 
				PerfStatsDTOMapperUtils.DEFAULT_Filter1_PendingCountDTOMapper : null;
		if (res == null) {
			// TODO... not implemented... assuming default:
			res = PerfStatsDTOMapperUtils.DEFAULT_Filter1_PendingCountDTOMapper;
		}
		return res;
	}
	
	
	private PathSubscriptions getOrCreateSubscriptionsPerPath(String subscriptionPath) {
		PathSubscriptions res = path2subscriptionEntries.get(subscriptionPath);
		if (res == null) {
			res = new PathSubscriptions(subscriptionPath);
			path2subscriptionEntries.put(subscriptionPath, res);
		}
		return res;
	}

	public void publishEventsForSubscriptions() {
		PathSubscriptions[] subscrsCopy;
		synchronized(lockSubscriptions) {
			if (path2subscriptionEntries.isEmpty()) {
				return;
			}
			subscrsCopy = path2subscriptionEntries.values().toArray(new PathSubscriptions[path2subscriptionEntries.size()]);
		}
		for(PathSubscriptions subscr : subscrsCopy) {
			PropTreeNode propTreeNode = subscr.pathRef.get();
			if (propTreeNode == null) {
				String[] path = subscr.path.split("/");
				propTreeNode = metricsStatsTreeRegistry.getPropTreeNodeByPathOrNull(path);
				subscr.pathRef = new WeakReference<PropTreeNode>(propTreeNode);
			}
			if (propTreeNode == null) {
				continue; // node does not exist (yet?) ... ignore
			}
			
			SubscriptionEntry[] subscEntries = subscr.subscriptionEntries;
			for(SubscriptionEntry subscEntry : subscEntries) {
				Object lastValue = subscEntry.lastPublishedValue;
				PropTreeNodeDTO newValue = subscEntry.dtoMapper.map(propTreeNode);
				boolean eq = (newValue == lastValue) // both null or ==
						|| (newValue != null && lastValue != null && newValue.equals(lastValue));
				if (eq) {
					continue;
				}
				subscEntry.lastPublishedValue = newValue;
				
				// convert object to json??
				byte[] payload;
				try {
					payload = jsonConverter.writer().writeValueAsBytes(newValue);
				} catch (JsonProcessingException ex) {
					LOG.error("Failed to convert object to json", ex);
					continue;
				}
				
				WebSocketMessage<?> message = new TextMessage(payload);
				try {
					subscEntry.owner.webSocketSession.sendMessage(message);
				} catch (IOException e) {
					subscEntry.owner.sendFailedCount++;
				}
			}
			
		}
	}


}
