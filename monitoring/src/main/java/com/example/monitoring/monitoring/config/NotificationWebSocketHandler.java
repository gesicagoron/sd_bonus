package com.example.monitoring.monitoring.config;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Component
public class NotificationWebSocketHandler extends TextWebSocketHandler {

    private final Set<WebSocketSession> sessions = new HashSet<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        System.out.println("DEBUG: WebSocket session established: " + session.getId());
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        System.out.println("WebSocket session closed: " + session.getId());
    }


    public void sendNotification(String message) {
        System.out.println("DEBUG: Active WebSocket sessions: " + sessions.size());
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    System.out.println("DEBUG: Sending message to session: " + session.getId());
                    session.sendMessage(new TextMessage(message));
                    System.out.println("DEBUG: Message sent successfully to session: " + session.getId());
                } catch (IOException e) {
                    System.err.println("Error sending notification to session " + session.getId() + ": " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.out.println("DEBUG: Session " + session.getId() + " is closed and cannot receive messages.");
            }
        }
    }



}
