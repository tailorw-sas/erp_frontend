package com.kynsoft.finamer.creditcard.application.command.manualTransaction.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

@Getter
public class CreateManualTransactionMessage implements ICommandMessage {

    private final String command = "CREATE_MANUAL_TRANSACTION";

}
