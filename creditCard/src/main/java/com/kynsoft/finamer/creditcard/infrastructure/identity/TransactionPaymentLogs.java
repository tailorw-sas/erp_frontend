package com.kynsoft.finamer.creditcard.infrastructure.identity;

import com.kynsof.audit.infrastructure.core.annotation.RemoteAudit;
import com.kynsof.audit.infrastructure.listener.AuditEntityListener;
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
@EntityListeners(AuditEntityListener.class)
@RemoteAudit(name = "vcc_transaction_payment_logs",id="7b2ea5e8-e34c-47eb-a811-25a54fe2c604")
public class TransactionPaymentLogs implements Serializable {

    @Id
    @Column(name = "id", unique = true)
    private UUID id;
    @Column(name = "transaction_id", unique = true)
    private UUID transactionId;

    @Column(name= "merchant_request", columnDefinition = "TEXT")
    private String merchantRequest;

    @Column(name= "merchant_response", columnDefinition = "TEXT")
    private String merchantResponse;

    @CreationTimestamp
    @Column(name = "created_at",nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_processed")
    private  Boolean isProcessed;

    @Column(name = "transaction_number")
    private Long transactionNumber;

    public TransactionPaymentLogs(TransactionPaymentLogsDto dto) {
        this.id = dto.getId();
        this.transactionId = dto.getTransactionId();
        this.merchantRequest = dto.getMerchantRequest();
        this.merchantResponse = dto.getMerchantResponse();
        this.isProcessed = dto.getIsProcessed();
        this.transactionNumber = dto.getTransactionNumber();
    }
    public TransactionPaymentLogsDto toAggregate(){
        return new TransactionPaymentLogsDto(
                 id,transactionId, merchantRequest, merchantResponse, isProcessed, transactionNumber
        );
    }
}
