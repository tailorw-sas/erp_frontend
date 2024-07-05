package com.kynsoft.finamer.settings.domain.dto;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManagerMerchantCurrencyDto {

    private UUID id;
    private ManagerMerchantDto managerMerchant;
    private ManagerCurrencyDto managerCurrency;
    private Double value;
    private String description;
    private Status status;

}
