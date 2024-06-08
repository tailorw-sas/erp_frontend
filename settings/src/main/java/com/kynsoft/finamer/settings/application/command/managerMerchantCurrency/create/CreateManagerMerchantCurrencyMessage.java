package com.kynsoft.finamer.settings.application.command.managerMerchantCurrency.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateManagerMerchantCurrencyMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_MANAGER_MERCHANT_CURRENCY";

    public CreateManagerMerchantCurrencyMessage(UUID id) {
        this.id = id;
    }

}
