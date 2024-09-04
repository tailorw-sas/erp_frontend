package com.kynsoft.finamer.payment.application.command.paymentDetailApplyDeposit.create;

import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreatePaymentDetailApplyDepositRequest {

    private Status status;
    private UUID employee;
    private UUID paymentDetail;
    private UUID transactionType;
    private UUID transactionTypeForAdjustment;
    private Double amount;
    private String remark;

    private UUID booking;
    private Boolean applyPayment;

}
