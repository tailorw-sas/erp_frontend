package com.kynsoft.finamer.creditcard.application.query.manageBankReconciliation;

import com.kynsoft.finamer.creditcard.domain.dto.ManageCreditCardTypeDto;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreditCardTypeToBankReconciliationResponse {

    private UUID id;
    private String code;
    private String name;

    public CreditCardTypeToBankReconciliationResponse(ManageCreditCardTypeDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
    }
}
