package org.sef4j.testwebapp.config;

import java.util.Map;

import org.sef4j.core.api.session.InOutEventsClientSessionManager;
import org.sef4j.core.util.factorydef.ObjectByDefRepositories;
import org.sef4j.springmsg.websocket.ClientSessionTransportWebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer implements WebSocketConfigurer{
    
    private static final Logger LOG = LoggerFactory.getLogger(WebSocketConfig.class);
    
    @Autowired
    public ObjectByDefRepositories sharedObjByDefRepositories;
    	 
    @Bean
    public InOutEventsClientSessionManager inOutEventsClientSessionManager() {
    	return new InOutEventsClientSessionManager(sharedObjByDefRepositories);
    }
    
    @Bean
    public ClientSessionTransportWebSocketHandler clientSessionTransportWebSocketHandler() {
    	return new ClientSessionTransportWebSocketHandler(inOutEventsClientSessionManager());
    }
    
    
	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config.enableSimpleBroker("/topic");
		config.setApplicationDestinationPrefixes("/app/async");
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/pendingCount")
    		.addInterceptors(new LoggerHandshakeInterceptor())
    		.withSockJS();
	}

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(clientSessionTransportWebSocketHandler(), "/pendingCount");
	}
	

	
	protected static class LoggerHandshakeInterceptor implements HandshakeInterceptor {
        
        @Override
        public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, 
                WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
            LOG.info("WebSocket beforeHandshake");
            return true;
        }
        
        @Override
        public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, 
                WebSocketHandler wsHandler, Exception exception) {
            LOG.info("WebSocket afterHandshake");
        }
    }
	
}