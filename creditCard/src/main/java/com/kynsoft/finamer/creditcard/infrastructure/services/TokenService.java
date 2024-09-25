package com.kynsoft.finamer.creditcard.infrastructure.services;

import com.kynsoft.finamer.creditcard.domain.dto.PaymentRequestDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class TokenService {

    private final SecretKey secretKey = Keys.hmacShaKeyFor("mi_clave_secreta_que_debe_tener_al_menos_256_bits".getBytes());

    public ResponseEntity<String> generateToken(PaymentRequestDto requestDto) {
        return ResponseEntity.ok(Jwts.builder()
                .claim("transactionId", requestDto.getTransactionUuid())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7)) // 24 hora * 7
                .signWith(secretKey)
                .compact()
        );
    }

    public Claims validateToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
