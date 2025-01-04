package com.kynsoft.finamer.payment.application.command.paymentDetail.undoReverseTransaction.apply;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.payment.application.query.objectResponse.PaymentResponse;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReverseTransactionApplyPaymentDetailMessage implements ICommandMessage {

    private PaymentResponse payment;

    public ReverseTransactionApplyPaymentDetailMessage(PaymentDto payment) {
        this.payment = new PaymentResponse(payment);
    }

}
