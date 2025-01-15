package com.example.chat.dto;

import java.time.LocalDateTime;

public class ChatResponseDTO {

    private String sender;
    private String receiver;
    private String content;
    private String timestamp;

    public ChatResponseDTO(String sender, String receiver, String content, String timestamp) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getContent() {
        return content;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
