package com.kynsoft.finamer.settings.application.command.manageAgencyType.update;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateManageAgencyTypeRequest {

    private Status status;
    private String name;
    private String description;
}
