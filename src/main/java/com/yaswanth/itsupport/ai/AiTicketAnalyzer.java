package com.yaswanth.itsupport.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import com.yaswanth.itsupport.dto.AiTicketAnalysisResponse;

import tools.jackson.databind.ObjectMapper;

@Service
public class AiTicketAnalyzer {

    private final ChatClient chatClient;

    public AiTicketAnalyzer(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public AiTicketAnalysisResponse analyzeTicket(String message) {

        String response = chatClient.prompt()
                .system("""
                        You are an IT Support Ticket Analyzer.

                        Analyze the user's IT problem and return ONLY valid JSON.

                        The JSON must contain exactly these fields:
                        category
                        priority
                        suggestion

                        Allowed categories:
                        NETWORK
                        HARDWARE
                        SOFTWARE
                        LOGIN
                        SECURITY
                        OTHER

                        Priority is determined by the application,
                        so do not use the priority for decision making.

                        Keep the suggestion short and practical.

                        Return ONLY JSON.

                        Example:
                        {
                          "category": "NETWORK",
                          "priority": "MEDIUM",
                          "suggestion": "Restart the laptop and reconnect to the office WiFi."
                        }
                        """)
                .user(message)
                .call()
                .content();

        return parseResponse(response);
    }

    private AiTicketAnalysisResponse parseResponse(String response) {

        response = response
                .replace("```json", "")
                .replace("```", "")
                .trim();

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper.readValue(
                    response,
                    AiTicketAnalysisResponse.class
            );
        } catch (Exception e) {
            throw new RuntimeException(
                    "Unable to parse AI response: " + response
            );
        }
    }
}