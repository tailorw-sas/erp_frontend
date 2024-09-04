package com.kynsoft.finamer.payment.application.command.payment.applyPayment;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ApplyPaymentRequest {

    private UUID payment;
    private List<UUID> invoices;
}
