package com.kynsoft.finamer.invoicing.application.command.incomeAdjustment.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteIncomeAdjustmentMessage implements ICommandMessage {

    private final UUID id;

    public DeleteIncomeAdjustmentMessage(UUID id) {
        this.id = id;
    }

}
