package com.kynsoft.finamer.payment.application.command.payment.createPaymentToCredit;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePaymentToCreditMessage implements ICommandMessage {

    private final String command = "CREATE_PAYMENT_TO_CREDIT";

    public CreatePaymentToCreditMessage() {}

}
