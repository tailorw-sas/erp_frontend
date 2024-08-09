package com.kynsoft.finamer.invoicing.application.command.incomeAdjustment.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import lombok.Setter;

@Getter
@Setter
public class CreateIncomeAdjustmentMessage implements ICommandMessage {

    private final String command = "CREATE_INCOME";

    public CreateIncomeAdjustmentMessage() {
    }

}
