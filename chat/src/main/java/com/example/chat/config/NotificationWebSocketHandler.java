package com.example.chat.config;

import com.example.chat.entity.ChatMessage;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class NotificationWebSocketHandler extends TextWebSocketHandler {

    private static final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    public NotificationWebSocketHandler() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    // 📌 1. Gestionarea conexiunii
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String username = session.getUri().getQuery().split("=")[1];
        sessions.put(username, session);
        System.out.println("✅ Connection established with username: " + username);
    }

    // 📌 2. Gestionarea mesajelor
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println("DEBUG: Received message - " + message.getPayload());

        JsonNode jsonNode = objectMapper.readTree(message.getPayload());
        String type = jsonNode.has("type") ? jsonNode.get("type").asText() : "message";

        switch (type) {
            case "typing":
                handleTypingNotification(jsonNode);
                break;
            case "read-receipt":
                handleReadReceipt(jsonNode);
                break;
            case "message":
                handleChatMessage(jsonNode);
                break;
            default:
                System.out.println("⚠️ WARN: Unknown message type received: " + type);
                break;
        }
    }

    // 📌 3. Gestionarea mesajelor de chat
    private void handleChatMessage(JsonNode jsonNode) throws Exception {
        String sender = jsonNode.get("sender").asText();
        String receiver = jsonNode.get("receiver").asText();

        ChatMessage chatMessage = objectMapper.readValue(jsonNode.toString(), ChatMessage.class);

        WebSocketSession receiverSession = sessions.get(receiver);
        if (receiverSession != null && receiverSession.isOpen()) {
            receiverSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatMessage)));
            System.out.println("✅ Message sent to receiver - " + receiver);
        } else {
            System.out.println("⚠️ WARN: Receiver not found or connection closed.");
        }
    }

    // 📌 4. Gestionarea notificărilor de citire
    private void handleReadReceipt(JsonNode jsonNode) throws Exception {
        String sender = jsonNode.get("sender").asText();
        String receiver = jsonNode.get("receiver").asText();

        WebSocketSession receiverSession = sessions.get(receiver);

        if (receiverSession != null && receiverSession.isOpen()) {
            ObjectNode readReceipt = objectMapper.createObjectNode();
            readReceipt.put("type", "read-receipt");
            readReceipt.put("sender", sender);
            readReceipt.put("receiver", receiver);

            receiverSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(readReceipt)));
            System.out.println("✅ Read receipt sent to " + receiver);
        } else {
            System.out.println("⚠️ WARN: Receiver not found or connection closed.");
        }
    }

    // 📌 5. Gestionarea notificărilor de scriere (typing)
    private void handleTypingNotification(JsonNode jsonNode) throws Exception {
        String sender = jsonNode.get("sender").asText();
        String receiver = jsonNode.get("receiver").asText();

        WebSocketSession receiverSession = sessions.get(receiver);

        if (receiverSession != null && receiverSession.isOpen()) {
            ObjectNode typingNotification = objectMapper.createObjectNode();
            typingNotification.put("type", "typing");
            typingNotification.put("sender", sender);
            typingNotification.put("receiver", receiver);

            receiverSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(typingNotification)));
            System.out.println("📝 Typing notification sent to " + receiver);
        } else {
            System.out.println("⚠️ WARN: Typing notification failed - Receiver not found or connection closed.");
        }
    }

    // 📌 6. Gestionarea închiderii conexiunii
    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) {
        String username = sessions.entrySet().stream()
                .filter(entry -> entry.getValue().equals(session))
                .map(entry -> entry.getKey())
                .findFirst()
                .orElse(null);

        if (username != null) {
            sessions.remove(username);
            System.out.println("⚠️ Connection closed with username: " + username);
        }
    }

    // 📌 7. Gestionarea erorilor
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.out.println("❌ Transport error: " + exception.getMessage());
        session.close();
    }
}
