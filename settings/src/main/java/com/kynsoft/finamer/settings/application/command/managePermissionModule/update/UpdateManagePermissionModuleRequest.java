package com.kynsoft.finamer.settings.application.command.managePermissionModule.update;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateManagePermissionModuleRequest {

    private Status status;
    private String name;
    private String description;
    private Boolean isActive;

}
