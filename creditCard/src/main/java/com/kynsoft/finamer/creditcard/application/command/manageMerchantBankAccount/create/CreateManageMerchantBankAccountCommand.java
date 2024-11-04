package com.kynsoft.finamer.creditcard.application.command.manageMerchantBankAccount.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CreateManageMerchantBankAccountCommand implements ICommand {

    private UUID id;
    private Set<UUID> managerMerchant;
    private UUID managerBank;
    private String accountNumber;
    private String description;
    private Status status;
    private Set<UUID> creditCardTypes;

    public CreateManageMerchantBankAccountCommand(Set<UUID> managerMerchant, UUID managerBank, String accountNumber, String description, Status status, Set<UUID> creditCardTypes) {
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
