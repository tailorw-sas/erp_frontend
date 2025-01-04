package com.kynsoft.finamer.creditcard.application.command.transaction.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateTransactionMessage implements ICommandMessage {

    private final Long id;
    private final String message = "UPDATE_TRANSACTION";
}
