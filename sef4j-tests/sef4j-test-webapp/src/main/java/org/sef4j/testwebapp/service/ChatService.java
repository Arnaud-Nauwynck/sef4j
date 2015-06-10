package org.sef4j.testwebapp.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ChatService {

	private static final Logger LOG = LoggerFactory.getLogger(ChatService.class);
	
	public static class ChatMessage {
		private final String from;
		private final Date date;
		private final String text;

		public ChatMessage(String from, Date date, String text) {
			super();
			this.from = from;
			this.date = date;
			this.text = text;
		}
		
		public String getFrom() {
			return from;
		}
		public Date getDate() {
			return date;
		}
		public String getText() {
			return text;
		}
		
	}
	
	private static class ChatEntry {
		final String id;
		private List<ChatMessage> messages = new ArrayList<ChatMessage>();
		
		public ChatEntry(String id) {
			this.id = id;
		}
		
		public void addMessage(String from, Date date, String text) {
			messages.add(new ChatMessage(from, date, text));
		}
		
	}
	
	private Map<String,ChatEntry> chats = new HashMap<String,ChatEntry>();

	// ------------------------------------------------------------------------

	public ChatService() {
		createChat("chat0");
		createChat("chat1");
	}

	// ------------------------------------------------------------------------

	public Collection<String> findChats() {
		Collection<String> res = chats.keySet();
		return res;
	}

	public void createChat(String id) {
		LOG.info("createChat chatId:" + id);
		ChatEntry res = chats.get(id);
		if (res == null) {
			res = new ChatEntry(id);
			chats.put(id, res);
		}
	}

	public List<ChatMessage> findChatMessages(String id) {
		LOG.info("findChatMessages chatId:" + id);
		ChatEntry chat = safeGetChat(id);
		return chat.messages;
	}

	public void sendChatMessage(String id, String text) {
		LOG.info("sendChatMessage chatId:" + id + ", text:" + text);
		ChatEntry chat = safeGetChat(id);
		String from = "<<TODO currentUser>>";
		Date now = new Date();
		chat.addMessage(from, now, text);
	}

	private ChatEntry safeGetChat(String id) {
		ChatEntry chat = chats.get(id);
		if (chat == null) {
			throw new IllegalArgumentException("chat not found '" + id + "'");
		}
		return chat;
	}

}
