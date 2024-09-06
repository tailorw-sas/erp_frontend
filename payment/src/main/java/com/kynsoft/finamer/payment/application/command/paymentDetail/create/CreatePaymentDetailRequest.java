package com.kynsoft.finamer.payment.application.command.paymentDetail.create;

import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreatePaymentDetailRequest {

    private Status status;
    private UUID employee;
    private UUID payment;
    private UUID transactionType;
    private Double amount;
    private String remark;

    private UUID booking;
    private Boolean applyPayment;
}
