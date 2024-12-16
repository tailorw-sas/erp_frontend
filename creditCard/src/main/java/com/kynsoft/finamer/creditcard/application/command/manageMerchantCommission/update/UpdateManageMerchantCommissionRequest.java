package com.kynsoft.finamer.creditcard.application.command.manageMerchantCommission.update;

import com.kynsoft.finamer.creditcard.domain.dtoEnum.CalculationType;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class UpdateManageMerchantCommissionRequest {
    private UUID managerMerchant;
    private UUID manageCreditCartType;
    private Double commission;
    private CalculationType calculationType;
    private String description;
    private LocalDate fromDate;
    private LocalDate toDate;
    private Status status;
}
