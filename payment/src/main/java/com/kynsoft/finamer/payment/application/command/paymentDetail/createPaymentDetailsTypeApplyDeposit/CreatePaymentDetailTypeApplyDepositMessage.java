package com.kynsoft.finamer.payment.application.command.paymentDetail.createPaymentDetailsTypeApplyDeposit;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import lombok.Getter;

import lombok.Setter;

@Getter
@Setter
public class CreatePaymentDetailTypeApplyDepositMessage implements ICommandMessage {

    private PaymentDetailDto newDetailDto;

    public CreatePaymentDetailTypeApplyDepositMessage(PaymentDetailDto newDetailDto) {
        this.newDetailDto = newDetailDto;
    }

}
