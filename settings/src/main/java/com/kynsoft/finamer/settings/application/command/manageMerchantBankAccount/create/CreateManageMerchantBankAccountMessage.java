package com.kynsoft.finamer.settings.application.command.manageMerchantBankAccount.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateManageMerchantBankAccountMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_MANAGE_MERCHANT_BANK_ACCOUNT";

    public CreateManageMerchantBankAccountMessage(UUID id) {
        this.id = id;
    }

}
