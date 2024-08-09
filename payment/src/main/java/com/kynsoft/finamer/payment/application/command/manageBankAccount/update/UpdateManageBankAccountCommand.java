package com.kynsoft.finamer.payment.application.command.manageBankAccount.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateManageBankAccountCommand implements ICommand {

    private UUID id;
    private String accountNumber;
    private String status;
    private String nameOfBank;
    private UUID manageHotel;

    public UpdateManageBankAccountCommand(UUID id, String accountNumber, String status, String nameOfBank,UUID manageHotel) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.status = status;
        this.nameOfBank = nameOfBank;
        this.manageHotel=manageHotel;
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManageBankAccountMessage(id);
    }
}
