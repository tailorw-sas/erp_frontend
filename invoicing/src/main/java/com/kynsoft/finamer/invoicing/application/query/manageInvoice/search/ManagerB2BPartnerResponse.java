package com.kynsoft.finamer.invoicing.application.query.manageInvoice.search;

import com.kynsoft.finamer.invoicing.domain.dto.ManagerB2BPartnerDto;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ManagerB2BPartnerResponse {

    private UUID id;
    private String code;
    private String name;

    public ManagerB2BPartnerResponse(ManagerB2BPartnerDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
    }
}
