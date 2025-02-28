package com.kynsoft.finamer.invoicing.domain.dto;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageEmployeeDto implements Serializable {

    private UUID id;
    private String firstName;
    private String lastName;;
    private String email;
    private String phoneExtension;
    private List<ManageAgencyDto> manageAgencyList;
    private List<ManageHotelDto> manageHotelList;
}