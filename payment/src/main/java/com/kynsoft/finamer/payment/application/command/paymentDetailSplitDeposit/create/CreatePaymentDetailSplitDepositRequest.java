package com.kynsoft.finamer.payment.application.command.paymentDetailSplitDeposit.create;

import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreatePaymentDetailSplitDepositRequest {

    private Status status;
    private UUID paymentDetail;
    private UUID transactionType;
    private Double amount;
    private String remark;
}
