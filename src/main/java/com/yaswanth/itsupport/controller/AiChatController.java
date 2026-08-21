package com.yaswanth.itsupport.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yaswanth.itsupport.ai.AiChatService;
import com.yaswanth.itsupport.dto.AiTicketAnalysisResponse;

@RestController
@RequestMapping("/api/ai")
public class AiChatController {

    private final AiChatService aiChatService;

    public AiChatController(AiChatService aiChatService) {
        this.aiChatService = aiChatService;
    }

    @PostMapping("/chat")
    public String chat(@RequestParam String message) {
        return aiChatService.chat(message);
    }
    @PostMapping("/analyze-ticket")
    public AiTicketAnalysisResponse analyzeTicket(@RequestParam String message) {
        return aiChatService.analyzeTicket(message);
    }
}