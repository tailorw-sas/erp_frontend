package com.kynsoft.finamer.creditcard.domain.dto;

import com.kynsoft.finamer.creditcard.domain.dtoEnum.CalculationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
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
    private CalculationType calculationType;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String status;

}
