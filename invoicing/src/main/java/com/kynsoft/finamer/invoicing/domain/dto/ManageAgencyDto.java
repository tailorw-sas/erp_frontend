package com.kynsoft.finamer.invoicing.domain.dto;

import com.kynsoft.finamer.invoicing.domain.dtoEnum.EGenerationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageAgencyDto {

    private UUID id;
    private String code;
    private String name;
    private ManageClientDto client;
    private EGenerationType generationType;
    private String status;
    private String cif;
    private String address;
    private ManagerB2BPartnerDto sentB2BPartner;
    private ManageCityStateDto cityState;
    private ManagerCountryDto country;
    private String mailingAddress;
    private String zipCode;
    private String city;
    private Integer creditDay;
    private Boolean autoReconcile;
}
