package com.kynsoft.finamer.payment.application.command.paymentcloseoperation.updateAll;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

@Getter
public class UpdateAllPaymentCloseOperationMessage implements ICommandMessage {

    private final String command = "UPDATE_ALL";

    public UpdateAllPaymentCloseOperationMessage() {
    }

}
