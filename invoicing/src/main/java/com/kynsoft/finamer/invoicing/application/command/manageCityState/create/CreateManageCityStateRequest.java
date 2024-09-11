package com.kynsoft.finamer.invoicing.application.command.manageCityState.create;

import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageCityStateRequest {
    private String code;
    private String name;
    private String description;
    private Status status;
    private UUID country;
    private UUID timeZone;
}
