package com.kynsoft.finamer.settings.application.command.manageBankAccount.create;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageBankAccountRequest {

    private Status status;
    private String accountNumber;
    private UUID manageBank;
    private UUID manageHotel;
    private UUID manageAccountType;
    private String description;
}
