package com.kynsoft.finamer.creditcard.infrastructure.identity;

import com.kynsoft.finamer.creditcard.domain.dto.CardnetJobDto;
import com.kynsof.audit.infrastructure.core.annotation.RemoteAudit;
import com.kynsof.audit.infrastructure.listener.AuditEntityListener;
import jakarta.persistence.*;
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
@EntityListeners(AuditEntityListener.class)
@RemoteAudit(name = "vcc_cardnet_job",id="7b2ea5e8-e34c-47eb-a811-25a54fe2c604")
public class CardnetJob implements Serializable {

    @Id
    @Column(name = "id", unique = true)
    private UUID id;

    @Column(name = "transaction_id")
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
                id,transactionId, session, sessionKey, isProcessed, numberOfAttempts, createdAt
        );
    }
}