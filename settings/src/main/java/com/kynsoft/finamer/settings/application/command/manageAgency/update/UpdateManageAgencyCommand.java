package com.kynsoft.finamer.settings.application.command.manageAgency.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.EGenerationType;
import com.kynsoft.finamer.settings.domain.dtoEnum.ESentFileFormat;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UpdateManageAgencyCommand implements ICommand {

    private UUID id;
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

    public static UpdateManageAgencyCommand fromRequest(UpdateManageAgencyRequest request, UUID id){
        return new UpdateManageAgencyCommand(
                id, request.getStatus(), request.getName(), request.getCif(), request.getAgencyAlias(), request.getAudit(), request.getZipCode(), request.getAddress(), request.getMailingAddress(), request.getPhone(), request.getAlternativePhone(), request.getEmail(), request.getAlternativeEmail(), request.getContactName(), request.getAutoReconcile(), request.getCreditDay(), request.getRfc(), request.getValidateCheckout(), request.getBookingCouponFormat(), request.getDescription(), request.getCity(), request.getGenerationType(), request.getSentFileFormat(), request.getAgencyType(), request.getClient(), request.getSentB2BPartner(), request.getCountry(), request.getCityState()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManageAgencyMessage(id);
    }
}
