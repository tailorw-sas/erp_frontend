package com.kynsoft.finamer.creditcard.application.command.manageMerchantBankAccount.update;

import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class UpdateManageMerchantBankAccountRequest {
    private UUID managerMerchant;
    private UUID managerBank;
    private String accountNumber;
    private String description;
    private Status status;
    private Set<UUID> creditCardTypes;
}
