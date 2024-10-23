package com.kynsoft.finamer.creditcard.domain.dto;

import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageMerchantBankAccountDto {

    private UUID id;
    private Set<ManageMerchantDto> managerMerchant;
    private ManagerBankDto manageBank;
    private String accountNumber;
    private String description;
    private Status status;
    private Set<ManageCreditCardTypeDto> creditCardTypes;
}
