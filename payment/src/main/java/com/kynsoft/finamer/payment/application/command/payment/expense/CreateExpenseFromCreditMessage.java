package com.kynsoft.finamer.payment.application.command.payment.expense;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateExpenseFromCreditMessage implements ICommandMessage {

    private final UUID id;
    private final String command = "CREATE_EXPENSES_FROM_CREDIT_COMMAND";

    public CreateExpenseFromCreditMessage(UUID id){
        this.id = id;
    }
}
