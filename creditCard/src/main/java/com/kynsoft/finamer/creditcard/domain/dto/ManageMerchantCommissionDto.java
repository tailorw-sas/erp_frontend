package com.kynsoft.finamer.creditcard.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageMerchantCommissionDto {

    private UUID id;
    private ManageMerchantDto managerMerchant;
    private ManageCreditCardTypeDto manageCreditCartType;
    private Double commission;
    private String calculationType;

}
