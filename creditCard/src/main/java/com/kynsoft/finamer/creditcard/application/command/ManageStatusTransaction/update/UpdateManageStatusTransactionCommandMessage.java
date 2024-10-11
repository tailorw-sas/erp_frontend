package com.kynsoft.finamer.creditcard.application.command.ManageStatusTransaction.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Data;

@Data
public class UpdateManageStatusTransactionCommandMessage implements ICommandMessage {
    private final String result;

    public UpdateManageStatusTransactionCommandMessage(String result) {
        this.result = result;
    }
}
