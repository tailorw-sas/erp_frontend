package com.kynsoft.finamer.creditcard.application.query.manageBankReconciliation;

import com.kynsoft.finamer.creditcard.domain.dto.ManagerBankDto;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class ManageBankToBankReconciliationResponse {

    private UUID id;
    private String code;
    private String name;
    private String description;
    private Status status;

    public ManageBankToBankReconciliationResponse(ManagerBankDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.status = dto.getStatus();
    }
}
