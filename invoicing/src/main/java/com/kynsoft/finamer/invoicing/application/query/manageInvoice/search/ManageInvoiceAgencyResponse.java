package com.kynsoft.finamer.invoicing.application.query.manageInvoice.search;

import com.kynsoft.finamer.invoicing.application.query.objectResponse.ManageCityStateResponse;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.ManagerCountryResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAgencyDto;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ManageInvoiceAgencyResponse {
    private UUID id;
    private String code;
    private String name;
    private ManagerB2BPartnerResponse sentB2BPartner;
    private ManageClientResponse client;
    private ManagerCountryResponse country;
    private ManageCityStateResponse cityState;

    public ManageInvoiceAgencyResponse(ManageAgencyDto projection) {
        this.id = projection.getId();
        this.code = projection.getCode();
        this.name = projection.getName();
        this.sentB2BPartner = projection.getSentB2BPartner() != null ? new ManagerB2BPartnerResponse(projection.getSentB2BPartner()) : null;
        this.client = projection.getClient() != null ? new ManageClientResponse(projection.getClient()) : null;
        this.country = projection.getCountry() != null ? new ManagerCountryResponse(projection.getCountry()) : null;
        this.cityState = projection.getCityState() != null ? new ManageCityStateResponse(projection.getCityState()) : null;
    }

}
