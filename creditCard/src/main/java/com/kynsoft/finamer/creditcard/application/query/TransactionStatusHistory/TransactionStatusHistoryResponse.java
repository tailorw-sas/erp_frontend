package com.kynsoft.finamer.creditcard.application.query.TransactionStatusHistory;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.creditcard.domain.dto.TransactionStatusHistoryDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class TransactionStatusHistoryResponse implements IResponse {

    private UUID id;
    private TransactionToStatusHistoryResponse transaction;
    private String description;
    private LocalDateTime createdAt;
    private String employee;
    private TransactionStatusToStatusHistoryResponse transactionStatus;

    public TransactionStatusHistoryResponse(TransactionStatusHistoryDto dto){
        this.id = dto.getId();
        this.transaction = dto.getTransaction() != null ? new TransactionToStatusHistoryResponse(dto.getTransaction()) : null;
        this.description = dto.getDescription();
        this.createdAt = dto.getCreatedAt();
        this.employee = dto.getEmployee();
        this.transactionStatus = dto.getTransactionStatus() != null ? new TransactionStatusToStatusHistoryResponse(dto.getTransactionStatus()) : null;
    }
}
