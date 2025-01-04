package com.kynsoft.finamer.creditcard.application.command.manageMerchantCurrency.create;

import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageMerchantCurrencyRequest {
    private UUID managerMerchant;
    private UUID managerCurrency;
    private String value;
    private String description;
    private Status status;

}
