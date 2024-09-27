package com.kynsoft.finamer.payment.application.command.paymentDetail.undoApplyPayment;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UndoApplyPaymentDetailRequest {

    private UUID paymentDetail;
    private UUID booking;
}
