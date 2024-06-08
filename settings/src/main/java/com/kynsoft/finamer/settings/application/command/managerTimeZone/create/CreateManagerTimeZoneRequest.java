package com.kynsoft.finamer.settings.application.command.managerTimeZone.create;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateManagerTimeZoneRequest {
    private String code;
    private String name;
    private String description;
    private Double elapse;
    private Status status;
}
