package com.kynsoft.finamer.settings.application.command.manageMerchantBankAccount.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageMerchantBankAccountCommand implements ICommand {

    private UUID id;
    private UUID managerMerchant;
    private UUID managerBank;
    private String accountNumber;
    private String description;
    private Status status;
    private Set<UUID> creditCardTypes;

    public CreateManageMerchantBankAccountCommand(UUID managerMerchant, UUID managerBank, String accountNumber, String description, Status status, Set<UUID> creditCardTypes) {
        this.id = UUID.randomUUID();
        this.managerMerchant = managerMerchant;
        this.managerBank = managerBank;
        this.accountNumber = accountNumber;
        this.description = description;
        this.status = status;
        this.creditCardTypes = creditCardTypes;
    }

    public static CreateManageMerchantBankAccountCommand fromRequest(CreateManageMerchantBankAccountRequest request) {
        return new CreateManageMerchantBankAccountCommand(
                request.getManagerMerchant(),
                request.getManagerBank(),
                request.getAccountNumber(),
                request.getDescription(),
                request.getStatus(),
                request.getCreditCardTypes()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageMerchantBankAccountMessage(id);
    }
}
