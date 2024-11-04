package com.kynsoft.finamer.creditcard.domain.services;

import com.kynsoft.finamer.creditcard.application.command.manageBankReconciliation.create.CreateBankReconciliationAdjustmentRequest;
import com.kynsoft.finamer.creditcard.application.command.manageBankReconciliation.update.UpdateBankReconciliationAdjustmentRequest;
import com.kynsoft.finamer.creditcard.domain.dto.ManageBankReconciliationDto;
import com.kynsoft.finamer.creditcard.domain.dto.TransactionDto;

import java.util.List;
import java.util.Set;

public interface IBankReconciliationAdjustmentService {

    List<Long> createAdjustments(List<CreateBankReconciliationAdjustmentRequest> adjustmentRequest, Set<TransactionDto> transactionList);

    List<Long> createAdjustments(List<UpdateBankReconciliationAdjustmentRequest> adjustmentRequest, ManageBankReconciliationDto reconciliationDto);
}
