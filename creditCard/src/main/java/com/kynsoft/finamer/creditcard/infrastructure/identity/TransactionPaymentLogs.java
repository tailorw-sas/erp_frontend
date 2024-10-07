package com.kynsoft.finamer.creditcard.infrastructure.identity;

import com.kynsoft.finamer.creditcard.domain.dto.TransactionPaymentLogsDto;
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
import java.time.LocalDate;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "vcc_transaction_payment_logs")
public class TransactionPaymentLogs implements Serializable {

    @Id
    @Column(name = "id")
    private long id;

    private UUID transactionUuid;

    @Column(columnDefinition = "TEXT")
    private String html;

    private String merchantReturn;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDate createdAt;

    public TransactionPaymentLogs(TransactionPaymentLogsDto dto) {
        this.id = dto.getId();
        this.transactionUuid = dto.getTransactionUuid();
        this.html = dto.getHtml();
        this.merchantReturn = dto.getMerchantReturn();
        this.createdAt = dto.getCreatedAt();
    }

    public TransactionPaymentLogs toAggregate(){
        return new TransactionPaymentLogs(
                id, transactionUuid, html, merchantReturn, createdAt
        );
    }
}
