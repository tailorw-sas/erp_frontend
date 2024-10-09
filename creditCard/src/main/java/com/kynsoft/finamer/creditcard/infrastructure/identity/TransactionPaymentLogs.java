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
    @Column(name = "transaction_id", unique = true)
    private UUID transactionId;

    @Column(name= "merchant_request" ,columnDefinition = "TEXT")
    private String merchantRequest;

    @Column(name= "merchant_response")
    private String merchantResponse;

    @CreationTimestamp
    @Column(name = "created_at",nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public TransactionPaymentLogs(TransactionPaymentLogsDto dto) {
        this.id = dto.getId();
        this.transactionId = dto.getTransactionId();
        this.merchantRequest = dto.getMerchantRequest();
        this.merchantResponse = dto.getMerchantResponse();

    }
    public TransactionPaymentLogsDto toAggregate(){
        return new TransactionPaymentLogsDto(
                 id,transactionId, merchantRequest, merchantResponse
        );
    }
}
