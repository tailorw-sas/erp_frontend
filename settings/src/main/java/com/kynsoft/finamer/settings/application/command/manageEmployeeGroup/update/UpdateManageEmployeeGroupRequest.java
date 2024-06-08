package com.kynsoft.finamer.settings.application.command.manageEmployeeGroup.update;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateManageEmployeeGroupRequest {

    private Status status;
    private String name;
    private String description;

}
