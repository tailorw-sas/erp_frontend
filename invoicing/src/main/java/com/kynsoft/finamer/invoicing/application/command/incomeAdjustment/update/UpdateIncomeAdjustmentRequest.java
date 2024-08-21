package com.kynsoft.finamer.invoicing.application.command.incomeAdjustment.update;

import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateIncomeAdjustmentRequest {

    private Status status;
    private UUID income;
    private UUID transactionType;
    private UUID paymentTransactionType;
    private Double amount;
    private LocalDate date;
    private String remark;
}
