package com.kynsoft.finamer.payment.application.command.paymentDetail.createPaymentDetailsTypeDeposit;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import lombok.Getter;

import lombok.Setter;

@Getter
@Setter
public class CreatePaymentDetailTypeDepositMessage implements ICommandMessage {

    private PaymentDetailDto newDetailDto;

    public CreatePaymentDetailTypeDepositMessage(PaymentDetailDto newDetailDto) {
        this.newDetailDto = newDetailDto;
    }

}
