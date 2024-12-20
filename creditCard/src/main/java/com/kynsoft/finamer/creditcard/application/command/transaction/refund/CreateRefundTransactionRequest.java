package com.kynsoft.finamer.creditcard.application.command.transaction.refund;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateRefundTransactionRequest {

    private Long parentId;
    private Boolean hasCommission;
    private Double amount;
    private String employee;
    private UUID employeeId;
}
