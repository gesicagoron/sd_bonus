package com.example.chat.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class JwtTokenValidatorService {

    @Value("${users.service.url}")
    private String userServiceUrl; // Adaugă URL-ul microserviciului users în application.properties

    private final RestTemplate restTemplate = new RestTemplate();

    public boolean validateToken(String token) {
        String url = userServiceUrl + "/api/auth/validate";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(token, headers);

        ResponseEntity<Boolean> response = restTemplate.postForEntity(url, request, Boolean.class);
        return response.getBody() != null && response.getBody();
    }

    public String getUsernameFromToken(String token) {
        String url = userServiceUrl + "/api/auth/get-username";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(token, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        return response.getBody();
    }
}
