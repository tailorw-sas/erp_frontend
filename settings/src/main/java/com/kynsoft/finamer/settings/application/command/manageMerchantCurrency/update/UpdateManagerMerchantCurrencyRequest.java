package com.kynsoft.finamer.settings.application.command.manageMerchantCurrency.update;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateManagerMerchantCurrencyRequest {
    private UUID managerMerchant;
    private UUID managerCurrency;
    private String value;
    private String description;
    private Status status;
}
