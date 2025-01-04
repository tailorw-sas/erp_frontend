package com.kynsoft.finamer.invoicing.domain.dto;

import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class IncomeAdjustmentDto implements Serializable {

    private UUID id;
    private Status status;
    private IncomeDto income;
    private ManageInvoiceTransactionTypeDto transactionType;
    private ManagePaymentTransactionTypeDto paymentTransactionType;
    private Double amount;
    private LocalDate date;
    private String remark;
}
