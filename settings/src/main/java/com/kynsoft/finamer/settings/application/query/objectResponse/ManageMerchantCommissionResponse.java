package com.kynsoft.finamer.settings.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageMerchantCommissionDto;
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
public class ManageMerchantCommissionResponse implements IResponse {

    private UUID id;
    private ManagerMerchantResponse managerMerchant;
    private ManageCreditCardTypeResponse manageCreditCartType;
    private Double commission;
    private String calculationType;
    private String description;
    private LocalDate fromDate;
    private LocalDate toDate;

    public ManageMerchantCommissionResponse(ManageMerchantCommissionDto dto) {
        this.id = dto.getId();
        this.managerMerchant = dto.getManagerMerchant() != null ? new ManagerMerchantResponse(dto.getManagerMerchant()) : null;
        this.manageCreditCartType = dto.getManageCreditCartType() != null ? new ManageCreditCardTypeResponse(dto.getManageCreditCartType()) : null;
        this.commission = dto.getCommission();
        this.calculationType = dto.getCalculationType();
        this.description = dto.getDescription();
        this.fromDate = dto.getFromDate();
        this.toDate = dto.getToDate().equals(LocalDate.parse("4000-12-31")) ? null : dto.getToDate();
    }

}
