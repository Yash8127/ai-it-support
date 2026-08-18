package com.yaswanth.itsupport.controller;

import com.yaswanth.itsupport.ai.AiChatService;
import org.springframework.web.bind.annotation.*;

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
}