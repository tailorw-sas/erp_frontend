package com.kynsoft.finamer.invoicing.application.query.manageInvoice.search;

import com.kynsoft.finamer.invoicing.domain.dto.projection.ManageAgencySimpleProjection;
import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageAgency;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ManageInvoiceAgencyResponse {
    private UUID id;
    private String code;
    private String name;

    public ManageInvoiceAgencyResponse(ManageAgency projection) {
        this.id = projection.getId();
        this.code = projection.getCode();
        this.name = projection.getName();
    }

}
