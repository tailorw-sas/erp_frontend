package com.kynsoft.finamer.invoicing.application.query.manageInvoice.search;

import com.kynsoft.finamer.invoicing.domain.dto.projection.ManageHotelSimpleProjection;
import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageHotel;
import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageInvoice;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ManageInvoiceHotelResponse {
    private UUID id;
    private String code;
    private String name;

    public ManageInvoiceHotelResponse(ManageHotel projection) {
        this.id = projection.getId();
        this.code = projection.getCode();
        this.name = projection.getName();
    }
}
