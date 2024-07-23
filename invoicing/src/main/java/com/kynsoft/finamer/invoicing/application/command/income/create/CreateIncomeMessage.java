package com.kynsoft.finamer.invoicing.application.command.income.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateIncomeMessage implements ICommandMessage {

    private UUID id;

    public CreateIncomeMessage(UUID id) {
        this.id = id;
    }

}
