package com.kynsoft.finamer.creditcard.application.command.manageMerchantBankAccount.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class UpdateManageMerchantBankAccountCommand implements ICommand {

    private UUID id;
    private UUID managerMerchant;
    private UUID managerBank;
    private String accountNumber;
    private String description;
    private Status status;
    private Set<UUID> creditCardTypes;

    public UpdateManageMerchantBankAccountCommand(UUID id, UUID managerMerchant, UUID managerBank, String accountNumber, String description, Status status, Set<UUID> creditCardTypes) {
        this.id = id;
        this.managerMerchant = managerMerchant;
        this.managerBank = managerBank;
        this.accountNumber = accountNumber;
        this.description = description;
        this.status = status;
        this.creditCardTypes = creditCardTypes;
    }

    public static UpdateManageMerchantBankAccountCommand fromRequest(UpdateManageMerchantBankAccountRequest request, UUID id) {
        return new UpdateManageMerchantBankAccountCommand(
                id,
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
        return new UpdateManageMerchantBankAccountMessage(id);
    }
}
