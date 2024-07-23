package com.kynsoft.finamer.invoicing.application.command.incomeAdjustment.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import java.util.UUID;
import lombok.Getter;

import lombok.Setter;

@Getter
@Setter
public class CreateIncomeAdjustmentMessage implements ICommandMessage {

    private final UUID id;

    public CreateIncomeAdjustmentMessage(UUID id) {
        this.id = id;
    }

}
