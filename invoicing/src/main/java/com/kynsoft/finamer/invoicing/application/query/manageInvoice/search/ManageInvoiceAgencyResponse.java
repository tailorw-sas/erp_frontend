package com.kynsoft.finamer.invoicing.application.query.manageInvoice.search;

import com.kynsoft.finamer.invoicing.domain.dto.projection.ManageAgencySimpleProjection;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ManageInvoiceAgencyResponse {
    private UUID id;
    private String code;
    private String name;

    public ManageInvoiceAgencyResponse(ManageAgencySimpleProjection projection) {
        this.id = projection.getId();
        this.code = projection.getCode();
        this.name = projection.getName();
    }

}
