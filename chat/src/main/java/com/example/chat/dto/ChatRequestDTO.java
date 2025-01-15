package com.example.chat.dto;

import jakarta.validation.constraints.NotBlank;

public class ChatRequestDTO {

    @NotBlank(message = "Sender cannot be blank")
    private String sender;

    @NotBlank(message = "Receiver cannot be blank")
    private String receiver;

    @NotBlank(message = "Content cannot be blank")
    private String content;

    // Getteri și Setteri
    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
