package com.kynsoft.finamer.settings.application.query.manageAgencyContact;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageAgencyContactDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageAgencyContactResponse implements IResponse  {

    private UUID id;
    private ManageAgencyContactAgencyResponse manageAgency;
    private ManageAgencyContactRegionResponse manageRegion;
    private String emailContact;
    private List<ManageAgencyContactHotelResponse> manageHotel;

    public ManageAgencyContactResponse(ManageAgencyContactDto dto){
        this.id = dto.getId();
        this.manageAgency = dto.getManageAgency() != null ? new ManageAgencyContactAgencyResponse(dto.getManageAgency()) : null;
        this.manageRegion = dto.getManageRegion() != null ? new ManageAgencyContactRegionResponse(dto.getManageRegion()) : null;
        this.emailContact = dto.getEmailContact();
        this.manageHotel = dto.getManageHotel() != null ? dto.getManageHotel().stream().map(ManageAgencyContactHotelResponse::new).collect(Collectors.toList()) : null;
    }
}
