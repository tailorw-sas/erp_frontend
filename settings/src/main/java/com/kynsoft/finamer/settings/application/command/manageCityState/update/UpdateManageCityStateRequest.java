package com.kynsoft.finamer.settings.application.command.manageCityState.update;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateManageCityStateRequest {
    private String name;
    private String description;
    private Status status;
    private UUID country;
    private UUID timeZone;
}
