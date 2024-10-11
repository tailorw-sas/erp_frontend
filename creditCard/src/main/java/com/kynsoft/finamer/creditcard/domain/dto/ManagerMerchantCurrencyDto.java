package com.kynsoft.finamer.creditcard.domain.dto;

import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
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
    private ManageMerchantDto managerMerchant;
    private ManagerCurrencyDto managerCurrency;
    private String value;
    private String description;
    private Status status;

}
