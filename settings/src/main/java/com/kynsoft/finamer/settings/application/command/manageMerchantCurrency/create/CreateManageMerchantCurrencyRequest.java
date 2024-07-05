package com.kynsoft.finamer.settings.application.command.manageMerchantCurrency.create;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateManageMerchantCurrencyRequest {
    private UUID managerMerchant;
    private UUID managerCurrency;
    private Double value;
    private String description;
    private Status status;

}
