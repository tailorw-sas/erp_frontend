package com.kynsoft.finamer.invoicing.application.query.objectResponse;

import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceStatusDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageInvoiceStatusResponse {

    private UUID id;
    private String code;
    private String name;
    private Boolean showClone;

    public ManageInvoiceStatusResponse(ManageInvoiceStatusDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.showClone = dto.getShowClone();
    }

}
