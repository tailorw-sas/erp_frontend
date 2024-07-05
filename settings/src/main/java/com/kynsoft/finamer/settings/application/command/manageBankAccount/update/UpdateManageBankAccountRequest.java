package com.kynsoft.finamer.settings.application.command.manageBankAccount.update;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateManageBankAccountRequest {

    private Status status;
    private UUID manageBank;
    private UUID manageHotel;
    private UUID manageAccountType;
    private String description;
}
