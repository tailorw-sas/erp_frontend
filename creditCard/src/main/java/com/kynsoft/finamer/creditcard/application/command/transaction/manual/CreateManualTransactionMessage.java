package com.kynsoft.finamer.creditcard.application.command.transaction.manual;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

@Getter
public class CreateManualTransactionMessage implements ICommandMessage {

    private final Long id;

    private final String command = "CREATE_MANUAL_TRANSACTION";

    public CreateManualTransactionMessage(Long id) {
        this.id = id;
    }

}
