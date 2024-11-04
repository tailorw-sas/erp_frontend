package com.kynsoft.finamer.creditcard.application.command.manageMerchantBankAccount.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteManageMerchantBankAccountMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_MANAGE_MERCHANT_BANK_ACCOUNT";

    public DeleteManageMerchantBankAccountMessage(UUID id) {
        this.id = id;
    }
}
