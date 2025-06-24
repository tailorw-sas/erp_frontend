package com.kynsoft.finamer.invoicing.application.command.incomeAdjustment.create;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateIncomeAdjustment {

    private UUID transactionType;
    private Double amount;
    private String date;
    private String remark;
}
