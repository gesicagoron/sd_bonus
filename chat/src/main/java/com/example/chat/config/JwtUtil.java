package com.example.chat.config;

import com.example.chat.service.JwtKeyProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Autowired
    private JwtKeyProvider jwtKeyProvider;

    public String getUsernameFromJWT(String token) {
        return Jwts.parser()
                .setSigningKey(jwtKeyProvider.getPublicKey())
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtKeyProvider.getPublicKey())
                    .parseClaimsJws(token)
                    .getBody();
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}
