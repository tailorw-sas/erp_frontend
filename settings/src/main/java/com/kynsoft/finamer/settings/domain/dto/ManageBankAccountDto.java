package com.kynsoft.finamer.settings.domain.dto;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
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
    private ManagerBankDto manageBank;
    private ManageHotelDto manageHotel;
    private ManagerAccountTypeDto manageAccountType;
    private String description;
}
