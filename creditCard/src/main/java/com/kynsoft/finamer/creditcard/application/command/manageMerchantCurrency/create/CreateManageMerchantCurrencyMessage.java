package com.kynsoft.finamer.creditcard.application.command.manageMerchantCurrency.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateManageMerchantCurrencyMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_MANAGER_MERCHANT_CURRENCY";

    public CreateManageMerchantCurrencyMessage(UUID id) {
        this.id = id;
    }

}
