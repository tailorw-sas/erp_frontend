package com.kynsoft.finamer.creditcard.infrastructure.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Service
public class TokenService {

    private final SecretKey secretKey = Keys.hmacShaKeyFor("mi_clave_secreta_que_debe_tener_al_menos_256_bits".getBytes());

    public String generateToken(UUID transactionUuid) {
        return Jwts.builder()
                .claim("transactionUuid", transactionUuid)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7)) // 24 horas * 7
                .signWith(secretKey)
                .compact();
    }

    public Claims validateToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
