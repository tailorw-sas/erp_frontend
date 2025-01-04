package com.kynsoft.finamer.invoicing.application.command.incomeAdjustment.create;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class NewIncomeAdjustmentRequest {

    private UUID transactionType;
    private Double amount;
    private LocalDate date;
    private String remark;
}
