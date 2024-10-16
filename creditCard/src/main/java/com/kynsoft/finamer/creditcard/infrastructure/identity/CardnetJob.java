package com.kynsoft.finamer.creditcard.infrastructure.identity;

import com.kynsoft.finamer.creditcard.domain.dto.CardnetJobDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity

@Table(name = "vcc_cardnet_job")
public class CardnetJob implements Serializable {

    @Id
    @Column(name = "id", unique = true)
    private UUID id;

    @Column(name = "transaction_id", unique = true)
    private UUID transactionId;

    private String session;

    @Column(name= "session_key")
    private String sessionKey;

    @Column(name= "is_processed")
    private Boolean isProcessed;

    @CreationTimestamp
    @Column(name = "created_at",nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "number_of_attempts")
    private Integer numberOfAttempts;

    public CardnetJob(CardnetJobDto dto) {
        this.id = dto.getId();
        this.transactionId = dto.getTransactionId();
        this.session = dto.getSession();
        this.sessionKey = dto.getSessionKey();
        this.isProcessed = dto.getIsProcessed();
        this.numberOfAttempts = dto.getNumberOfAttempts();

    }
    public CardnetJobDto toAggregate(){
        return new CardnetJobDto(
                id,transactionId, session, sessionKey, isProcessed, numberOfAttempts
        );
    }
}