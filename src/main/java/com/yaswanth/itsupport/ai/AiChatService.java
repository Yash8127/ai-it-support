package com.yaswanth.itsupport.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AiChatService {

	private final ChatClient chatClient;

	public AiChatService(ChatClient.Builder chatClientBuilder) {
		this.chatClient = chatClientBuilder.build();
	}

	public String chat(String message) {

		return chatClient.prompt().system("""
		        You are an AI IT Support Assistant.
		        Help users solve common IT problems such as WiFi,
		        network, software, login, and computer issues.

		        Give simple, practical troubleshooting steps.
		        Keep responses short and professional.
		        Ask questions when more information is needed.
		        Recommend IT support when the issue cannot be safely resolved.
		        """).user(message).call().content();
	}
}