package com.kynsoft.finamer.settings.infrastructure.projections;

import com.kynsoft.finamer.settings.domain.dtoEnum.EGenerationType;
import com.kynsoft.finamer.settings.domain.dtoEnum.ESentFileFormat;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.infrastructure.identity.*;
import lombok.Getter;

import java.util.UUID;

public class ManageAgencyProjection {

    @Getter
    private UUID id;

    @Getter
    private String code;

    @Getter
    private String cif;

    @Getter
    private String agencyAlias;

    @Getter
    private Boolean audit;

    @Getter
    private String zipCode;

    @Getter
    private String address;

    @Getter
    private String mailingAddress;

    @Getter
    private String phone;

    @Getter
    private String alternativePhone;

    @Getter
    private String email;

    @Getter
    private String alternativeEmail;

    @Getter
    private String contactName;

    @Getter
    private Boolean autoReconcile;

    @Getter
    private Integer creditDay;

    @Getter
    private String rfc;

    @Getter
    private Boolean validateCheckout;

    @Getter
    private String bookingCouponFormat;

    @Getter
    private String description;

    @Getter
    private String city;

    @Getter
    private Boolean isDefault;

    @Getter
    private EGenerationType generationType;

    @Getter
    private ESentFileFormat sentFileFormat;

    @Getter
    private ManageAgencyType agencyType;

    @Getter
    private ManageClient client;

    @Getter
    private ManageB2BPartner sentB2BPartner;

    @Getter
    private ManageCountry country;

    @Getter
    private ManageCityState cityState;

    @Getter
    private Status status;

    @Getter
    private String name;

    public ManageAgencyProjection(UUID id,
                        String code,
                        String cif,
                        String agencyAlias,
                        Boolean audit,
                        String zipCode,
                        String address,
                        String mailingAddress,
                        String phone,
                        String alternativePhone,
                        String email,
                        String alternativeEmail,
                        String contactName,
                        Boolean autoReconcile,
                        Integer creditDay,
                        String rfc,
                        Boolean validateCheckout,
                        String bookingCouponFormat,
                        String description,
                        String city,
                        Boolean isDefault,
                        EGenerationType generationType,
                        ESentFileFormat sentFileFormat,
                        ManageAgencyType agencyType,
                        ManageClient client,
                        ManageB2BPartner sentB2BPartner,
                        ManageCountry country,
                        ManageCityState cityState,
                        Status status,
                        String name){
        this.id = id;
        this.code = code;
        this.cif = cif;
        this.agencyAlias = agencyAlias;
        this.audit = audit;
        this.zipCode = zipCode;
        this.address = address;
        this.mailingAddress = mailingAddress;
        this.phone = phone;
        this.alternativePhone = alternativePhone;
        this.email = email;
        this.alternativeEmail = alternativeEmail;
        this.contactName = contactName;
        this.autoReconcile = autoReconcile;
        this.creditDay = creditDay;
        this.rfc = rfc;
        this.validateCheckout = validateCheckout;
        this.bookingCouponFormat = bookingCouponFormat;
        this.description = description;
        this.city = city;
        this.isDefault = isDefault;
        this.generationType = generationType;
        this.sentFileFormat = sentFileFormat;
        this.agencyType = agencyType;
        this.client = client;
        this.sentB2BPartner = sentB2BPartner;
        this.country = country;
        this.cityState = cityState;
        this.status = status;
        this.name = name;
    }

    public ManageAgencyProjection(UUID id,
                                    String code,
                                    Status status,
                                    String name){
        this.id = id;
        this.code = code;
        this.status = status;
        this.name = name;
    }
}
