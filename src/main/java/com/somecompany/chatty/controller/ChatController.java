package com.somecompany.chatty.controller;

import com.somecompany.chatty.model.UserResponse;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;

import static com.somecompany.chatty.model.UserResponse.GroupType.PRIVATE;
import static com.somecompany.chatty.model.UserResponse.GroupType.PUBLIC;
import static com.somecompany.chatty.model.UserResponse.MessageType.CHAT;
import static com.somecompany.chatty.model.UserResponse.MessageType.JOIN;
import static com.somecompany.chatty.utils.Constants.NEW_USER_JOINED;
import static java.lang.String.valueOf;
import static java.time.LocalDateTime.now;

@Controller
public class ChatController {

	@MessageMapping("/connect")
	@SendTo("/topic/connect")
	public UserResponse connect(@RequestParam String username, SimpMessageHeaderAccessor headerAccessor) {
		headerAccessor.getSessionAttributes().put("username", username);
		UserResponse userResponse = new UserResponse(username, NEW_USER_JOINED, JOIN, PUBLIC);
		return userResponse;
	}

	@MessageMapping("/message")
	@SendTo("/topic/message")
	public UserResponse getMessage(@Payload UserResponse userResponse) {

		userResponse.setMtype(CHAT);
		userResponse.setGtype(PUBLIC);
		userResponse.setTime(valueOf(now()));

		return userResponse;
	}

	@MessageMapping("{groupid}/connect")
	@SendTo("/topic/{groupid}/connect")
	public UserResponse connect(@DestinationVariable String groupid, @RequestParam String username,
                                SimpMessageHeaderAccessor headerAccessor) {
		headerAccessor.getSessionAttributes().put("username", username);
		UserResponse userResponse = new UserResponse(username, NEW_USER_JOINED, JOIN, PRIVATE);
		return userResponse;
	}

	@MessageMapping("{groupid}/message")
	@SendTo("/topic/{groupid}/message")
	public UserResponse getMessage(@DestinationVariable String groupid, @Payload UserResponse userResponse) {

		userResponse.setMtype(CHAT);
		userResponse.setGtype(PRIVATE);
		userResponse.setTime(valueOf(now()));

		return userResponse;
	}
}
