package com.example.chat.controller;

import com.example.chat.dto.ChatRequestDTO;
import com.example.chat.dto.ChatResponseDTO;
import com.example.chat.service.ChatService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/send")
    public ChatResponseDTO sendMessage(@RequestBody ChatRequestDTO request) {
        return chatService.saveMessage(request);
    }


}
