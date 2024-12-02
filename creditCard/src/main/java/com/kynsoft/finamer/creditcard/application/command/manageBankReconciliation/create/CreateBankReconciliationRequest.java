package com.kynsoft.finamer.creditcard.application.command.manageBankReconciliation.create;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class CreateBankReconciliationRequest {

    private UUID merchantBankAccount;
    private UUID hotel;
    private Double amount;
    private Double detailsAmount;
    private LocalDateTime paidDate;
    private String remark;
    private Set<Long> transactions;
    private List<CreateBankReconciliationAdjustmentRequest> adjustmentTransactions;
    private String employee;
}
