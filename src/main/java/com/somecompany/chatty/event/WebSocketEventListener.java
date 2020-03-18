package com.somecompany.chatty.event;

import com.somecompany.chatty.model.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.logging.Logger;

import static com.somecompany.chatty.model.ChatDetails.count;
import static com.somecompany.chatty.model.UserResponse.GroupType.PUBLIC;
import static com.somecompany.chatty.model.UserResponse.MessageType.LEAVE;
import static com.somecompany.chatty.utils.Constants.USER_LEFT;
import static java.time.LocalDateTime.now;

@Component
public class WebSocketEventListener {

	@Autowired
    SimpMessagingTemplate simpMessagingTemplate;

	private final Logger logger = Logger.getLogger(WebSocketEventListener.class.getName());

	@EventListener(SessionConnectEvent.class)
	public void handleWebsocketConnectListener(SessionConnectEvent event) {
		count = count + 1;
		logger.info("Received a new web socket connection : " + now());
	}

	@EventListener(SessionDisconnectEvent.class)
	public void handleWebsocketDisconnectListener(SessionDisconnectEvent event) {

		count = count > 0 ? count - 1 : 0;

		SimpMessageHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
		String username = (String) headerAccessor.getSessionAttributes().get("username");

		if (username != null) {

			UserResponse userResponse = new UserResponse(username, USER_LEFT, LEAVE, PUBLIC);
			simpMessagingTemplate.convertAndSend("/topic/message", userResponse);
		}
	}
}
