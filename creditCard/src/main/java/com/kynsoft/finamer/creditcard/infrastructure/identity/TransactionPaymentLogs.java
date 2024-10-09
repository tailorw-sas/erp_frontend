package com.kynsoft.finamer.creditcard.infrastructure.identity;

import com.kynsoft.finamer.creditcard.domain.dto.TransactionPaymentLogsDto;
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
@Table(name = "vcc_transaction_payment_logs")
public class TransactionPaymentLogs implements Serializable {

    @Id
    @Column(name = "id", unique = true)
    private UUID id;

    private UUID transactionId;

    @Column(columnDefinition = "TEXT")
    private String html;

    private String merchantReturn;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updatedAt;

    public TransactionPaymentLogs(TransactionPaymentLogsDto dto) {
        this.id = dto.getId();
        this.transactionId = dto.getTransactionId();
        this.html = dto.getHtml();
        this.merchantReturn = dto.getMerchantReturn();

    }
    public TransactionPaymentLogsDto toAggregate(){
        return new TransactionPaymentLogsDto(
                 id,transactionId, html, merchantReturn
        );
    }
}
