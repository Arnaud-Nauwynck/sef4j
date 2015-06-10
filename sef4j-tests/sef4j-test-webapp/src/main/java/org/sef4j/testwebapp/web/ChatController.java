package org.sef4j.testwebapp.web;

import java.util.Collection;
import java.util.List;

import org.sef4j.core.api.def.ioevenchain.InputEventChainDef;
import org.sef4j.core.api.session.SubscriptionResponseDTO;
import org.sef4j.core.util.Handle;
import org.sef4j.springmsg.websocket.ClientSessionTransportWebSocketHandler;
import org.sef4j.testwebapp.service.ChatService;
import org.sef4j.testwebapp.service.ChatService.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.WebSocketSession;

@RestController
@RequestMapping("/app/rest/chat")
public class ChatController {

	private static final Logger LOG = LoggerFactory.getLogger(ChatController.class);

	@Autowired
	protected ChatService chatService;
	
	@Autowired 
	protected ClientSessionTransportWebSocketHandler clientSessionWSHandler;

	
	@RequestMapping(value="", method=RequestMethod.GET)
	public Collection<String> findChats() {
		return chatService.findChats();
	}

	@RequestMapping(value="/create/{id}", method=RequestMethod.POST)
	public void createChat(@PathVariable("id") String id) {
		chatService.createChat(id);
	}

	@RequestMapping(value="/{id}/messages", method=RequestMethod.GET)
	public List<ChatMessage> findChatMessages(@PathVariable("id") String id) {
		return chatService.findChatMessages(id);
	}

	@RequestMapping(value="/{id}/sendMessage", method=RequestMethod.POST)
	public void sendChatMessage(@PathVariable("id") String id, @RequestBody String text) {
		chatService.sendChatMessage(id, text);
	}

    @MessageMapping(value="/chat/{id}/subscribe")
    @SendToUser(broadcast=false)
    public SubscriptionResponseDTO addChatSubscription(WebSocketSession wsSession, @PathVariable("id") String id) {
    	InputEventChainDef def = null; // TODO
		Handle res = clientSessionWSHandler.addSubscription(wsSession, def, null, null, null);
    	return new SubscriptionResponseDTO(res);
    }
	
    @MessageMapping(value="/chat/{id}/unsubscribe")
    @SendToUser(broadcast=false)
    public SubscriptionResponseDTO removeChatSubscription(WebSocketSession wsSession,
    		@PathVariable("id") String id, //useless.. cf handleId!
    		@RequestBody int subscrId) {
    	Handle subscrHandleId = new Handle(subscrId);
		clientSessionWSHandler.removeSubscription(wsSession, subscrHandleId);
    	return new SubscriptionResponseDTO(true, "OK");
    }

}
