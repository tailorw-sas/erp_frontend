package com.kynsoft.finamer.creditcard.application.command.transaction.adjustment;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

@Getter
public class CreateAdjustmentTransactionMessage implements ICommandMessage {

    private final Long id;

    private final String command = "CREATE_ADJUSTMENT_TRANSACTION";

    public CreateAdjustmentTransactionMessage(Long id) {
        this.id = id;
    }
}
