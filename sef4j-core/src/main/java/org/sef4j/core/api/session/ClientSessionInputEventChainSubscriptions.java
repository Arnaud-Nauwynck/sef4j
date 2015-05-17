package org.sef4j.core.api.session;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.sef4j.core.api.EventSender;
import org.sef4j.core.api.def.ioevenchain.InputEventChainDef;
import org.sef4j.core.api.ioeventchain.InputEventChain;
import org.sef4j.core.api.ioeventchain.InputEventChain.ListenerHandle;
import org.sef4j.core.util.CopyOnWriteUtils;
import org.sef4j.core.util.Handle;
import org.sef4j.core.util.HandleGenerator;
import org.sef4j.core.util.MapUtils;
import org.sef4j.core.util.factorydef.ObjectByDefRepositories;
import org.sef4j.core.util.factorydef.SharedRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

/**
 * class to manage InputEventChain subscriptions per ClientSession
 * => redirect events listener from InputEventChain to sessionTransport 
 * 
 * <PRE>
 *  ObjectByDefRepositories
 *  +-----------+                                               SessionManager          
 *  |           |  <--- getOrCreateByDef()                      +------------+           
 *  +-----------+       \                                       |            |         
 *       |               \             (this)Suscriptions       +------------+
 *       |                 -----        +------------+                <>               
 *     InputEventChain          _       |            |                 |               
 *      Factory                |\       +------------+  <-             \/               
 *       .                       \           <>           \     ClientSession
 *       . new()                  \           |            \    +-----------+
 *       \/       <-- addEventListener()      \/             -- |           | -----> SessionTransport 
 * InputEventChain                      SubscriptionEntry   --> |           |         (WebSocket, JMS, ..)                   
 * +-----+                              +--------------+   /    +-----------+           
 * |     |       <--------------------  |SharedRef     |  -
 * |     |         sendEvent()          |listenerHandle|
 * +-----+             ---->            +--------------+
 *                 sendEvents()
 *                     ---->
 *              <-- removeEventListener()                                
 *              
 *              
 *              
 * </PRE>
 */
public class ClientSessionInputEventChainSubscriptions {

	private static final Logger LOG = LoggerFactory.getLogger(ClientSessionInputEventChainSubscriptions.class);
	
	/** class representing 1 subscription entry */
	private static class SubscriptionEntry {
		ClientSessionInputEventChainSubscriptions owner;
		SharedRef<InputEventChain<?>> inputEventChainHandle;
		ListenerHandle<?> listenerHandle;
		
		SubscriptionEntry(ClientSessionInputEventChainSubscriptions owner, 
				SharedRef<InputEventChain<?>> inputEventChainHandle) {
			this.owner = owner;
			this.inputEventChainHandle = inputEventChainHandle;
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		public void start() {
			if (listenerHandle == null) {
				EventSender<?> targetEventListener = owner.ownerClientSession.getAppService().getAppEventSender();
				InputEventChain<?> inputEventChain = inputEventChainHandle.getObject();
				this.listenerHandle = inputEventChain.registerEventListener((EventSender) targetEventListener);
			}
		}
		
		public void stop() {
			if (listenerHandle != null) {
				listenerHandle.close();
				this.listenerHandle = null;
			}
		}
		
		public void close() {
			if (listenerHandle != null) {
				stop();
			}
			if (inputEventChainHandle != null) {
				inputEventChainHandle.close();
				this.inputEventChainHandle = null;
				this.owner = null;
			}
		}
	}
	
	protected InOutEventsClientSession ownerClientSession;
	
	private ImmutableMap<Handle,SubscriptionEntry> subscriptions = ImmutableMap.of();

	private Object subscriptionsLock = new Object();

	private HandleGenerator handleGenerator = new HandleGenerator();
	
	private ObjectByDefRepositories sharedObjByDefRepositories;
	
	// ------------------------------------------------------------------------

	public ClientSessionInputEventChainSubscriptions(InOutEventsClientSession ownerClientSession) {
		this.ownerClientSession = ownerClientSession;
		this.sharedObjByDefRepositories = ownerClientSession.getOwnerSessionManager().getSharedObjByDefRepositories();
	}

	// ------------------------------------------------------------------------

	protected ObjectByDefRepositories getSharedObjByDefRepositories() {
		return sharedObjByDefRepositories;
	}
	
	public Handle addSubscription(InputEventChainDef def, Object key, String displayName, Map<String,Object> options) {
		SharedRef<InputEventChain<?>> inputEventChainHandle = 
				sharedObjByDefRepositories.getOrCreateByDef(def, key);
		SubscriptionEntry subscriptionEntry = new SubscriptionEntry(this, inputEventChainHandle);
		boolean skipStart = MapUtils.mapGetBooleanOption(options, "skipStart", false);
		if (! skipStart) {
			subscriptionEntry.start();
		}
		Handle res = handleGenerator.generate();
		synchronized(subscriptionsLock) {
			subscriptions = CopyOnWriteUtils.newWithPut(subscriptions, res, subscriptionEntry);
		}
		return res;
	}

	public void removeSubscription(Handle handle) {
		SubscriptionEntry subscriptionEntry = getSubscriptionOrThrow(handle);
		synchronized(subscriptionsLock) {
			subscriptions = CopyOnWriteUtils.newWithRemove(subscriptions, handle);
		}
		subscriptionEntry.close();
	}

	public void startSubscription(Handle handle) {
		SubscriptionEntry subscriptionEntry = getSubscriptionOrThrow(handle);
		subscriptionEntry.start();
	}
	
	public void stopSubscription(Handle handle) {
		SubscriptionEntry subscriptionEntry = getSubscriptionOrThrow(handle);
		subscriptionEntry.stop();
	}
	
	public List<SubscriptionResponseDTO> handleSubscriptionCommands(List<SubscriptionCommandDTO> cmds) {
		List<SubscriptionResponseDTO> res = new ArrayList<SubscriptionResponseDTO>();
    	LOG.info("handleSubscriptionCommandsRequest for " + ownerClientSession.getSessionDisplayName());
		for(SubscriptionCommandDTO cmd : cmds) {
			SubscriptionResponseDTO resp = handleSubscriptionCommand(cmd);
			res.add(resp);
		}
		return res;
	}

	public SubscriptionResponseDTO handleSubscriptionCommand(SubscriptionCommandDTO cmd) {
		SubscriptionResponseDTO response = new SubscriptionResponseDTO();
		response.setClientCommandId(cmd.getOptClientCommandId());
		response.setStatus(true);
		try {
			String cmdVerb = cmd.getCommand();
			String subscriptionDisplayName = cmd.getDisplayName();
			Object optKey = cmd.getOptKey();
			InputEventChainDef def = (InputEventChainDef) cmd.getDef();
			Map<String, Object> cmdOptions = cmd.getOptions();

			SubscriptionEntry subscriptionEntry = null;
			Handle subscriptionId = cmd.getSubscriptionId();
			if (subscriptionId != null) {
				subscriptionEntry = subscriptions.get(subscriptionId);
				if (subscriptionEntry == null) {
					response.setStatus(false);
					response.setReasonText("subscription handle not found");
					return response;
				}
			}
			
			if ("ADD".equalsIgnoreCase(cmdVerb)) {
				addSubscription(def, optKey, subscriptionDisplayName, cmdOptions);
			} else if ("REMOVE".equalsIgnoreCase(cmdVerb)) {
				removeSubscription(subscriptionId);
			} else if ("START".equalsIgnoreCase(cmdVerb)) {
				startSubscription(subscriptionId);
			} else if ("STOP".equalsIgnoreCase(cmdVerb)) {
				stopSubscription(subscriptionId);
			
			} else {
				response.setStatus(false);
				LOG.warn("unrecognised subscriptionCommand: " + cmd + " .. ignore");
				response.setReasonText("ERROR: unrecognised command " + cmd);
			}
		} catch(Exception ex) {
			response.setStatus(false);
			response.setReasonText("Failed: " + ex.getMessage());
		}
		return response;
	}

	private SubscriptionEntry getSubscriptionOrThrow(Handle handle) {
		SubscriptionEntry subscriptionEntry = subscriptions.get(handle);
		if (subscriptionEntry == null) {
			throw new IllegalArgumentException("subscription not found for handle");
		}
		return subscriptionEntry;
	}

	// ------------------------------------------------------------------------

	@Override
	public String toString() {
		return "ClientSessionInputEventChainSubscriptions [" + 
				subscriptions.size() + " elt(s)"
				+ "]";
	}

	
	
}
