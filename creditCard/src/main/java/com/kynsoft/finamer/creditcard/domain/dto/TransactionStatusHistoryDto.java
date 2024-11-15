package com.kynsoft.finamer.creditcard.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TransactionStatusHistoryDto {

    private UUID id;
    private TransactionDto transaction;
    private String description;
    private LocalDateTime createdAt;
    private String employee;
    private ManageTransactionStatusDto transactionStatus;
}
