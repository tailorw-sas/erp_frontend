package com.kynsoft.finamer.creditcard.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ManageBankReconciliationDto {

    private UUID id;
    private Long reconciliationId;
    private ManageMerchantBankAccountDto merchantBankAccount;
    private ManageHotelDto hotel;
    private Double amount;
    private Double detailsAmount;
    private LocalDateTime paidDate;
    private String remark;
    private ManageReconcileTransactionStatusDto reconcileStatus;
    private Set<TransactionDto> transactions;
}
