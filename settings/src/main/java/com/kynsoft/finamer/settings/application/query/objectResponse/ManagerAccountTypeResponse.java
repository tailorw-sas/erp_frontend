package com.kynsoft.finamer.settings.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.settings.domain.dto.ManagerAccountTypeDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class ManagerAccountTypeResponse implements IResponse {

    private UUID id;
    private String code;
    private String name;
    private String description;
    private Status status;
    private boolean moduleVcc;
    private boolean modulePayment;

    public ManagerAccountTypeResponse(ManagerAccountTypeDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.status = dto.getStatus();
        this.moduleVcc = dto.isModuleVcc();
        this.modulePayment = dto.isModulePayment();
    }

}
