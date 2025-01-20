package com.kynsoft.finamer.invoicing.domain.dto;

import java.io.Serializable;
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
public class ManageAgencyContactDto implements Serializable {

    private UUID id;
    private ManageAgencyDto manageAgency;
    private ManageRegionDto manageRegion;
    private List<ManageHotelDto> manageHotel;
    private String emailContact;
}
