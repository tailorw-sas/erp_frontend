package com.kynsoft.finamer.settings.application.command.manageActionLog.update;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateManageActionLogRequest {

    private String description;
    private Status status;
    private String name;
}
