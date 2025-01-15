package com.example.chat.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.security.Key;
import javax.crypto.spec.SecretKeySpec;

@Component
public class JwtKeyProvider {

    @Value("${users.service.url}")
    private String userServiceUrl;

    private Key publicKey;

    public Key getPublicKey() {
        if (publicKey == null) {
            RestTemplate restTemplate = new RestTemplate();
            String keyBase64 = restTemplate.getForObject(userServiceUrl + "/api/auth/public-key", String.class);
            byte[] decodedKey = Base64.getDecoder().decode(keyBase64);
            publicKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "HmacSHA512");
        }
        return publicKey;
    }
}
