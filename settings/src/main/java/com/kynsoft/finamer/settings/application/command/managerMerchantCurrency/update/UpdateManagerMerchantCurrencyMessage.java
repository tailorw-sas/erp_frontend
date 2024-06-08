package com.kynsoft.finamer.settings.application.command.managerMerchantCurrency.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateManagerMerchantCurrencyMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_MANAGER_MERCHANT_CURRENCY";

    public UpdateManagerMerchantCurrencyMessage(UUID id) {
        this.id = id;
    }

}
