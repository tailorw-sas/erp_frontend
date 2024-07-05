package com.kynsoft.finamer.payment.application.command.paymentDetail.update;

import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdatePaymentDetailRequest {

    private Status status;
    private UUID payment;
    private UUID transactionType;
    private Double amount;
    private String remark;
}
