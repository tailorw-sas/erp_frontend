package com.kynsoft.finamer.creditcard.application.command.manageStatusTransactionBlue.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Data;

@Data
public class UpdateManageStatusTransactionBlueCommandMessage implements ICommandMessage {
    private final String result;

    public UpdateManageStatusTransactionBlueCommandMessage(String result){
        this.result = result;
    }
}
