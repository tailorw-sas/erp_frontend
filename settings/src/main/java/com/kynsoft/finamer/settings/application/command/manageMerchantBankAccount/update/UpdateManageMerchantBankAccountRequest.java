package com.kynsoft.finamer.settings.application.command.manageMerchantBankAccount.update;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

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
