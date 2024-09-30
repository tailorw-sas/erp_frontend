package com.kynsoft.finamer.payment.application.command.paymentDetail.undoApplyPayment;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.payment.application.query.objectResponse.PaymentResponse;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import lombok.Getter;

import lombok.Setter;

@Getter
@Setter
public class UndoApplyPaymentDetailMessage implements ICommandMessage {

    private PaymentResponse payment;

    public UndoApplyPaymentDetailMessage(PaymentDto payment) {
        this.payment = new PaymentResponse(payment);
    }

}
