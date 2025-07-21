package com.kynsoft.finamer.insis.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageEmployeeDto {

    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    public LocalDateTime updatedAt;
    private List<ManageHotelDto> manageHotelList;
    private List<ManageAgencyDto> manageAgencyList;
}
