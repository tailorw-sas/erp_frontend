package com.kynsoft.finamer.settings.application.command.manageCityState.create;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

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
