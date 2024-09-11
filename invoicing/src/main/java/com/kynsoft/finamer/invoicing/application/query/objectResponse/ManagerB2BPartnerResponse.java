package com.kynsoft.finamer.invoicing.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;

import com.kynsoft.finamer.invoicing.domain.dto.ManagerB2BPartnerDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class ManagerB2BPartnerResponse implements IResponse {

    private UUID id;
    private String code;
    private String name;
    private String description;
    private Status status;


    public ManagerB2BPartnerResponse(ManagerB2BPartnerDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.status = dto.getStatus();

    }

}
