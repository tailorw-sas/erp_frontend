package com.kynsoft.finamer.invoicing.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.invoicing.domain.dto.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageTraidingCompaniesResponse implements IResponse {
    private UUID id;
    private String code;
    private Boolean isApplyInvoice;
    private Long autogen_code;
    private String cif;
    private String address;
    private String company;

    public ManageTraidingCompaniesResponse(ManageTradingCompaniesDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.isApplyInvoice = dto.getIsApplyInvoice();
        this.autogen_code = dto.getAutogen_code();
        this.cif = dto.getCif();
        this.address = dto.getAddress();
        this.company = dto.getCompany();
    }

    
}
