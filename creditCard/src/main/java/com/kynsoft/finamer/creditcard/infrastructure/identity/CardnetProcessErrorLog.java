package com.kynsoft.finamer.creditcard.infrastructure.identity;

import com.kynsoft.finamer.creditcard.domain.dto.CardnetProcessErrorLogDto;
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

@Table(name = "vcc_cardnet_process_error_logs")
public class CardnetProcessErrorLog implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction_id")
    private UUID transactionId;

    private String session;

    @CreationTimestamp
    @Column(name = "created_at",nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    private String error;

    public CardnetProcessErrorLog(CardnetProcessErrorLogDto dto) {
        this.id = dto.getId();
        this.transactionId = dto.getTransactionId();
        this.session = dto.getSession();
        this.error = dto.getError();

    }
    public CardnetProcessErrorLogDto toAggregate(){
        return new CardnetProcessErrorLogDto(
                id,transactionId, session, error
        );
    }

}
