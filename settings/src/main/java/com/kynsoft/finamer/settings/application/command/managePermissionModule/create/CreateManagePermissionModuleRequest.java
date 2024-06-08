package com.kynsoft.finamer.settings.application.command.managePermissionModule.create;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateManagePermissionModuleRequest {

    private String code;
    private String description;
    private Status status;
    private String name;
    private Boolean isActive;
}
