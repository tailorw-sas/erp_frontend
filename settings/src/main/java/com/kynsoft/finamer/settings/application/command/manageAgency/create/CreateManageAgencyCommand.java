package com.kynsoft.finamer.settings.application.command.manageAgency.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.EGenerationType;
import com.kynsoft.finamer.settings.domain.dtoEnum.ESentFileFormat;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageAgencyCommand implements ICommand {

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
    private UUID agencyType;
    private UUID client;
    private UUID sentB2BPartner;
    private UUID country;
    private UUID cityState;


    public CreateManageAgencyCommand(String code, Status status, String name, String cif, String agencyAlias, Boolean audit, String zipCode, String address, String mailingAddress, String phone, String alternativePhone, String email, String alternativeEmail, String contactName, Boolean autoReconcile, Integer creditDay, String rfc, Boolean validateCheckout, String bookingCouponFormat, String description, String city, EGenerationType generationType, ESentFileFormat sentFileFormat, UUID agencyType, UUID client, UUID sentB2BPartner, UUID country, UUID cityState) {
        this.id = UUID.randomUUID();
        this.code = code;
        this.status = status;
        this.name = name;
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
        this.generationType = generationType;
        this.sentFileFormat = sentFileFormat;
        this.agencyType = agencyType;
        this.client = client;
        this.sentB2BPartner = sentB2BPartner;
        this.country = country;
        this.cityState = cityState;
    }

    public static CreateManageAgencyCommand fromRequest(CreateManageAgencyRequest request) {
        return new CreateManageAgencyCommand(
                request.getCode(), request.getStatus(), request.getName(), request.getCif(), request.getAgencyAlias(), request.getAudit(), request.getZipCode(), request.getAddress(), request.getMailingAddress(), request.getPhone(), request.getAlternativePhone(), request.getEmail(), request.getAlternativeEmail(), request.getContactName(), request.getAutoReconcile(), request.getCreditDay(), request.getRfc(), request.getValidateCheckout(), request.getBookingCouponFormat(), request.getDescription(), request.getCity(), request.getGenerationType(), request.getSentFileFormat(), request.getAgencyType(), request.getClient(), request.getSentB2BPartner(), request.getCountry(), request.getCityState()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageAgencyMessage(id);
    }
}
