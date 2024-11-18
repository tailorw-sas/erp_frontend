package com.kynsoft.finamer.creditcard.application.command.manageBankReconciliation.update;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class UpdateBankReconciliationRequest {

    private LocalDateTime paidDate;
    private String remark;
    private Set<Long> transactions;
    private List<UpdateBankReconciliationAdjustmentRequest> adjustmentTransactions;
    private UUID reconcileStatus;
    private Double amount;
    private String employee;
}
