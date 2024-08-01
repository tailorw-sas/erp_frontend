package com.kynsoft.finamer.payment.application.command.paymentDetail.delete;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeletePaymentDetailRequest {

    private UUID employee;
    private UUID id;
}
