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
    private String status;
    private String nameOfBank;

    public CreateManageBankAccountCommand(UUID id, String accountNumber, String status, String nameOfBank) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.status = status;
        this.nameOfBank = nameOfBank;
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageBankAccountMessage(id);
    }
}
