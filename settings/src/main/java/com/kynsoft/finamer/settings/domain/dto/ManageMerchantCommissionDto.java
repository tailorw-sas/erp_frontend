package com.kynsoft.finamer.settings.domain.dto;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
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
    private ManagerMerchantDto managerMerchant;
    private ManageCreditCardTypeDto manageCreditCartType;
    private Double commission;
    private String calculationType;
    private String description;
    private LocalDate fromDate;
    private LocalDate toDate;
    private Status status;

}
