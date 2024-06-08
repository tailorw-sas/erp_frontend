package com.kynsoft.finamer.settings.application.command.managerTimeZone.update;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateManagerTimeZoneRequest {
    private String name;
    private String description;
    private Double elapse;
    private Status status;
}
