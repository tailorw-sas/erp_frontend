package com.kynsoft.finamer.payment.application.command.manageBankAccount.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageBankAccountCommand implements ICommand {

    private UUID id;
    private String accountNumber;

    public CreateManageBankAccountCommand(UUID id, String accountNumber) {
        this.id = id;
        this.accountNumber = accountNumber;
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageBankAccountMessage(id);
    }
}
