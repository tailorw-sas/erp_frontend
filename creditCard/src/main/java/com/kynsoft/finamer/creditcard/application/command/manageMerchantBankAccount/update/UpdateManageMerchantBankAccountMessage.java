package com.kynsoft.finamer.creditcard.application.command.manageMerchantBankAccount.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateManageMerchantBankAccountMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_MANAGE_MERCHANT_BANK_ACCOUNT";

    public UpdateManageMerchantBankAccountMessage(UUID id) {
        this.id = id;
    }

}
