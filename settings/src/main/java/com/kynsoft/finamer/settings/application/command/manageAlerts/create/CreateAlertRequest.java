package com.kynsoft.finamer.settings.application.command.manageAlerts.create;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAlertRequest {
    private String code;
    private String name;
    private Boolean popup;
    private Status status;
    private String description;
}
