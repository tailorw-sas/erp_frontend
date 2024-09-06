package com.kynsoft.finamer.payment.application.command.payment.applyPayment;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import lombok.Setter;

@Getter
@Setter
public class ApplyPaymentMessage implements ICommandMessage {

    private final String msg = "OK";

    public ApplyPaymentMessage() {
    }

}
