package com.kynsoft.finamer.creditcard.application.command.manageMerchantBankAccount.create;

import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class CreateManageMerchantBankAccountRequest {
    private UUID managerMerchant;
    private UUID managerBank;
    private String accountNumber;
    private String description;
    private Status status;
    private Set<UUID> creditCardTypes;
}
