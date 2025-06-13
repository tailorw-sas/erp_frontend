package com.kynsoft.finamer.invoicing.domain.core;

import com.kynsoft.finamer.invoicing.domain.dto.ManagePaymentTransactionTypeDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class IncomeAdjustment {

    private ManagePaymentTransactionTypeDto transactionType;
    private Double amount;
    private LocalDate date;
    private String remark;
}
