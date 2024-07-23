package com.kynsoft.finamer.invoicing.application.command.income.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateIncomeMessage implements ICommandMessage {

    private final UUID id;

    public UpdateIncomeMessage(UUID id) {
        this.id = id;
    }

}
