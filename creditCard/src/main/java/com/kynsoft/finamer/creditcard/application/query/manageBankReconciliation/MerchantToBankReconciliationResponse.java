package com.kynsoft.finamer.creditcard.application.query.manageBankReconciliation;

import com.kynsoft.finamer.creditcard.domain.dto.ManageMerchantDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MerchantToBankReconciliationResponse {

    private UUID id;
    private String code;
    private String description;

    public MerchantToBankReconciliationResponse(ManageMerchantDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.description = dto.getDescription();
    }
}
