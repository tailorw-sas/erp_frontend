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
    @Column(name = "id")
    private UUID id;

    private UUID transactionUuid;

    @Column(columnDefinition = "TEXT")
    private String html;

    private String merchantReturn;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public TransactionPaymentLogs(TransactionPaymentLogsDto dto) {
        this.id = dto.getId();
        this.transactionUuid = dto.getTransactionUuid();
        this.html = dto.getHtml();
        this.merchantReturn = dto.getMerchantReturn();

    }
    public TransactionPaymentLogs toAggregate(){
        return new TransactionPaymentLogs(
                 id,transactionUuid, html, merchantReturn,createdAt
        );
    }
}
