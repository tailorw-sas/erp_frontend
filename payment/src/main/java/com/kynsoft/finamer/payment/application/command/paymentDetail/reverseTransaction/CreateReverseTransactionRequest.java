package com.kynsoft.finamer.payment.application.command.paymentDetail.reverseTransaction;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateReverseTransactionRequest {

    private UUID paymentDetail;
    private UUID employee;
}
