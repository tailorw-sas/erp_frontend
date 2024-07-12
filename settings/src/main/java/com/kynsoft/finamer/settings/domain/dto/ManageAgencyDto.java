package com.kynsoft.finamer.settings.domain.dto;

import com.kynsoft.finamer.settings.domain.dtoEnum.EGenerationType;
import com.kynsoft.finamer.settings.domain.dtoEnum.ESentFileFormat;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
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
    private Status status;
    private String name;
    private String cif;
    private String agencyAlias;
    private Boolean audit;
    private String zipCode;
    private String address;
    private String mailingAddress;
    private String phone;
    private String alternativePhone;
    private String email;
    private String alternativeEmail;
    private String contactName;
    private Boolean autoReconcile;
    private Integer creditDay;
    private String rfc;
    private Boolean validateCheckout;
    private String bookingCouponFormat;
    private String description;
    private String city;
    private EGenerationType generationType;
    private ESentFileFormat sentFileFormat;
    private ManageAgencyTypeDto agencyType;
    private ManageClientDto client;
    private ManagerB2BPartnerDto sentB2BPartner;
    private ManagerCountryDto country;
    private ManageCityStateDto cityState;
    private Boolean isDefault;
}
