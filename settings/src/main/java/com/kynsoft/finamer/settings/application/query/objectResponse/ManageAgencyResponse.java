package com.kynsoft.finamer.settings.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.EGenerationType;
import com.kynsoft.finamer.settings.domain.dtoEnum.ESentFileFormat;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.infrastructure.projections.ManageAgencyProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageAgencyResponse implements IResponse, Serializable {

    private  UUID id;
    private  String code;
    private  Status status;
    private  String name;
    private  String cif;
    private  String agencyAlias;
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
    private ManageAgencyTypeResponse agencyType;
    private ManageClientResponse client;
    private ManagerB2BPartnerResponse sentB2BPartner;
    private ManagerCountryResponse country;
    private ManageCityStateResponse cityState;
    private Boolean isDefault;

    public ManageAgencyResponse(UUID id, String code, Status status, String name,  String agencyAlias) {
        this.id = id;
        this.code = code;
        this.status = status;
        this.name = name;
        this.agencyAlias = agencyAlias;
    }

    public ManageAgencyResponse(ManageAgencyDto dto){
        this.id = dto.getId();
        this.code = dto.getCode();
        this.status = dto.getStatus();
        this.name = dto.getName();
        this.cif = dto.getCif();
        this.agencyAlias = dto.getAgencyAlias();
        this.audit = dto.getAudit();
        this.zipCode = dto.getZipCode();
        this.address = dto.getAddress();
        this.mailingAddress = dto.getMailingAddress();
        this.phone = dto.getPhone();
        this.alternativePhone = dto.getAlternativePhone();
        this.email = dto.getEmail();
        this.alternativeEmail = dto.getAlternativeEmail();
        this.contactName = dto.getContactName();
        this.autoReconcile = dto.getAutoReconcile();
        this.creditDay = dto.getCreditDay();
        this.rfc = dto.getRfc();
        this.validateCheckout = dto.getValidateCheckout();
        this.bookingCouponFormat = dto.getBookingCouponFormat();
        this.description = dto.getDescription();
        this.city = dto.getCity();
        this.generationType = dto.getGenerationType();
        this.sentFileFormat = dto.getSentFileFormat();
        this.agencyType = dto.getAgencyType() != null ? new ManageAgencyTypeResponse(dto.getAgencyType()) : null;
        this.client = dto.getClient() != null ? new ManageClientResponse(dto.getClient()) : null;
        this.sentB2BPartner = dto.getSentB2BPartner() != null ? new ManagerB2BPartnerResponse(dto.getSentB2BPartner()) : null;
        this.country = dto.getCountry() != null ? new ManagerCountryResponse(dto.getCountry()) : null;
        this.cityState = dto.getCityState() != null ? new ManageCityStateResponse(dto.getCityState()) : null;
        this.isDefault = dto.getIsDefault();
    }

    public ManageAgencyResponse(ManageAgencyProjection dto){
        this.id = dto.getId();
        this.code = dto.getCode();
        this.status = dto.getStatus();
        this.name = dto.getName();
        this.cif = dto.getCif();
        this.agencyAlias = dto.getAgencyAlias();
        this.audit = dto.getAudit();
        this.zipCode = dto.getZipCode();
        this.address = dto.getAddress();
        this.mailingAddress = dto.getMailingAddress();
        this.phone = dto.getPhone();
        this.alternativePhone = dto.getAlternativePhone();
        this.email = dto.getEmail();
        this.alternativeEmail = dto.getAlternativeEmail();
        this.contactName = dto.getContactName();
        this.autoReconcile = dto.getAutoReconcile();
        this.creditDay = dto.getCreditDay();
        this.rfc = dto.getRfc();
        this.validateCheckout = dto.getValidateCheckout();
        this.bookingCouponFormat = dto.getBookingCouponFormat();
        this.description = dto.getDescription();
        this.city = dto.getCity();
        this.generationType = dto.getGenerationType();
        this.sentFileFormat = dto.getSentFileFormat();
        //this.agencyType = dto.getAgencyType() != null ? new ManageAgencyTypeResponse(dto.getAgencyType()) : null;
        //this.client = dto.getClient() != null ? new ManageClientResponse(dto.getClient()) : null;
        //this.sentB2BPartner = dto.getSentB2BPartner() != null ? new ManagerB2BPartnerResponse(dto.getSentB2BPartner()) : null;
        //this.country = dto.getCountry() != null ? new ManagerCountryResponse(dto.getCountry()) : null;
        //this.cityState = dto.getCityState() != null ? new ManageCityStateResponse(dto.getCityState()) : null;
        this.isDefault = dto.getIsDefault();
    }

}
