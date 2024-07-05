package com.kynsoft.finamer.settings.application.command.manageDepartmentGroup.update;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateManageDepartmentGroupRequest {

    private Status status;
    private String name;
    private String description;

}
