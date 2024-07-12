package com.kynsoft.finamer.creditcard.application.command.refundTransaction.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

@Getter
public class CreateRefundTransactionMessage implements ICommandMessage {

    private final Long id;

    private final String command = "CREATE_REFUND_TRANSACTION";

    public CreateRefundTransactionMessage(Long id) {
        this.id = id;
    }

}
