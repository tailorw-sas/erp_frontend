package com.kynsoft.finamer.payment.application.command.manageBankAccount.create;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    private UUID manageBank;
    private UUID manageHotel;

    public CreateManageBankAccountCommand(UUID id, String accountNumber, String status, String nameOfBank,UUID manageHotel) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.status = status;
        this.nameOfBank = nameOfBank;
        this.manageHotel=manageHotel;
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageBankAccountMessage(id);
    }
}
