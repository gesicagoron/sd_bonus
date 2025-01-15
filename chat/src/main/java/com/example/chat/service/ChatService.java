package com.example.chat.service;

import com.example.chat.dto.ChatRequestDTO;
import com.example.chat.dto.ChatResponseDTO;
import com.example.chat.entity.ChatMessage;
import com.example.chat.repository.ChatMessageRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ChatService {

    private final ChatMessageRepository repository;

    public ChatService(ChatMessageRepository repository) {
        this.repository = repository;
    }

    public ChatResponseDTO saveMessage(ChatRequestDTO request) {
        ChatMessage message = new ChatMessage();
        message.setSender(request.getSender());
        message.setReceiver(request.getReceiver());
        message.setContent(request.getContent());
        message.setTimestamp(String.valueOf(LocalDateTime.now()));

        repository.save(message);

        return new ChatResponseDTO(
                message.getSender(),
                message.getReceiver(),
                message.getContent(),
                message.getTimestamp()
        );
    }
}
