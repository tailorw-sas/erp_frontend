package com.kynsoft.finamer.settings.application.command.manageMerchantCommission.update;

import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateManageMerchantCommissionRequest {
    private UUID managerMerchant;
    private UUID manageCreditCartType;
    private Double commission;
    private String calculationType;
    private String description;
    private LocalDate fromDate;
    private LocalDate toDate;
}
