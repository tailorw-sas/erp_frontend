package com.kynsoft.finamer.payment.domain.dto;

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
    private String accountNumber;
    private String status;
    private String nameOfBank;
    private ManageHotelDto manageHotelDto;

}
