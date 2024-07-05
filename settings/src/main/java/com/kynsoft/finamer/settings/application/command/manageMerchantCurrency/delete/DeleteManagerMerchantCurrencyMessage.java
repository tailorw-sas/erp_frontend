package com.kynsoft.finamer.settings.application.command.manageMerchantCurrency.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteManagerMerchantCurrencyMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_MANAGER_MERCHANT_CURRENCY";

    public DeleteManagerMerchantCurrencyMessage(UUID id) {
        this.id = id;
    }

}
