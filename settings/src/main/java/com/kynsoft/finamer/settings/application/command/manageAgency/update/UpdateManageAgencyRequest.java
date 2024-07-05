package com.kynsoft.finamer.settings.application.command.manageAgency.update;

import com.kynsoft.finamer.settings.domain.dtoEnum.EGenerationType;
import com.kynsoft.finamer.settings.domain.dtoEnum.ESentFileFormat;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateManageAgencyRequest {


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
    private UUID agencyType;
    private UUID client;
    private UUID sentB2BPartner;
    private UUID country;
    private UUID cityState;
}
