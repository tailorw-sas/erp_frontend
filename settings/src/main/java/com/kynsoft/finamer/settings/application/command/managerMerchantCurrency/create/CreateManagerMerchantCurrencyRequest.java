package com.kynsoft.finamer.settings.application.command.managerMerchantCurrency.create;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateManagerMerchantCurrencyRequest {
    private UUID managerMerchant;
    private UUID managerCurrency;
    private Double value;
    private String description;
}
