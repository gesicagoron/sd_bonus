package com.example.chat.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class JwtUserValidatorService {

    @Value("${users.service.url}")
    private String userServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public boolean isUserValid(String username) {
        try {
            String url = userServiceUrl + "/person";
            ResponseEntity<Map[]> response = restTemplate.getForEntity(url, Map[].class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                for (Map<String, Object> user : response.getBody()) {
                    if (username.equals(user.get("name"))) {
                        return true; // Utilizatorul a fost găsit în baza de date
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to validate user via /person endpoint: " + e.getMessage());
        }
        return false;
    }
}
