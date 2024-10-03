package com.kynsoft.finamer.creditcard.application.command.manageRedirectTransactionPayment;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Data;

@Data
public class CreateRedirectTransactionPaymentCommandMessage implements ICommandMessage {
    private final String result;

    public CreateRedirectTransactionPaymentCommandMessage(String result) {
        this.result = result;
    }
}
