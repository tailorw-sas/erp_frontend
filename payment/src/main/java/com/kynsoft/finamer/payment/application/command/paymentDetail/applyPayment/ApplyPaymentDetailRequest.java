package com.kynsoft.finamer.payment.application.command.paymentDetail.applyPayment;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ApplyPaymentDetailRequest {

    private UUID paymentDetail;
    private UUID booking;
}
