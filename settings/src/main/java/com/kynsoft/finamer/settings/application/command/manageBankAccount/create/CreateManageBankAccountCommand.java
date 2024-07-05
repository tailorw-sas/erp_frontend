package com.kynsoft.finamer.settings.application.command.manageBankAccount.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageBankAccountCommand implements ICommand {

    private UUID id;
    private Status status;
    private String accountNumber;
    private UUID manageBank;
    private UUID manageHotel;
    private UUID manageAccountType;
    private String description;

    public CreateManageBankAccountCommand(Status status, String accountNumber,
                                          UUID manageBank, UUID manageHotel, UUID manageAccountType,
                                          String description) {
        this.id = UUID.randomUUID();
        this.status = status;
        this.accountNumber = accountNumber;
        this.manageBank = manageBank;
        this.manageHotel = manageHotel;
        this.manageAccountType = manageAccountType;
        this.description = description;
    }

    public static CreateManageBankAccountCommand fromRequest(CreateManageBankAccountRequest request){
        return new CreateManageBankAccountCommand(
                request.getStatus(), request.getAccountNumber(),
                request.getManageBank(), request.getManageHotel(), request.getManageAccountType(),
                request.getDescription()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageBankAccountMessage(id);
    }
}
