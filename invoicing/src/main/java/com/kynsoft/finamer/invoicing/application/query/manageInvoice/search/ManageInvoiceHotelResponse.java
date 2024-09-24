package com.kynsoft.finamer.invoicing.application.query.manageInvoice.search;

import com.kynsoft.finamer.invoicing.domain.dto.ManageHotelDto;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ManageInvoiceHotelResponse {
    private UUID id;
    private String code;
    private String name;
    private Boolean virtual;

    public ManageInvoiceHotelResponse(ManageHotelDto projection) {
        this.id = projection.getId();
        this.code = projection.getCode();
        this.name = projection.getName();
        this.virtual = projection.isVirtual();
    }
}
