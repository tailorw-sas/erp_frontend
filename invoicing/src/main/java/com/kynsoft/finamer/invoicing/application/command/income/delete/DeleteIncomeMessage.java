package com.kynsoft.finamer.invoicing.application.command.income.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteIncomeMessage implements ICommandMessage {

    private final UUID id;

    public DeleteIncomeMessage(UUID id) {
        this.id = id;
    }

}
