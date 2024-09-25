package com.kynsoft.finamer.payment.application.command.paymentDetail.applyOtherDeductions;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateApplyOtherDeductionsRequest {

    private UUID payment;
    private UUID transactionType;
    private String remark;

    private List<UUID> booking;
}
