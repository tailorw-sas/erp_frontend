package com.kynsoft.finamer.invoicing.domain.dto;


import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageBankAccountDto {

    private UUID id;
    private Status status;
    private String accountNumber;
    private ManageHotelDto manageHotel;
    private String description;
}
