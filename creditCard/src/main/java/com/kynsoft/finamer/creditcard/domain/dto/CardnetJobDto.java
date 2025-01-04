package com.kynsoft.finamer.creditcard.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CardnetJobDto {

    private UUID id;
    private UUID transactionId;
    private String session;
    private String sessionKey;
    private Boolean isProcessed;
    private Integer numberOfAttempts;
    private LocalDateTime createdAt;

    public CardnetJobDto(UUID id, UUID transactionId, String session, String sessionKey, Boolean isProcessed, Integer numberOfAttempts) {
        this.id = id;
        this.transactionId = transactionId;
        this.session = session;
        this.sessionKey = sessionKey;
        this.isProcessed = isProcessed;
        this.numberOfAttempts = numberOfAttempts;
    }

    public boolean isSessionExpired() {
        if (createdAt == null) {
            return true; // Si `createdAt` es nulo, se considera que la sesión expiró
        }

        LocalDateTime now = LocalDateTime.now();
        long minutesDifference = Duration.between(createdAt, now).toMinutes();

        return minutesDifference >= 30; // Retorna `true` si han pasado 30 minutos o más
    }
}
