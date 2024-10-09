package com.kynsoft.finamer.settings.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageAgencyContactDto {

    private UUID id;
    private ManageAgencyDto manageAgency;
    private ManageRegionDto manageRegion;
    private List<ManageHotelDto> manageHotel;
    private String emailContact;
}
