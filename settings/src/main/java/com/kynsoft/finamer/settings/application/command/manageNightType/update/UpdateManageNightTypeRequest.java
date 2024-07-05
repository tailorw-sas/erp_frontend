package com.kynsoft.finamer.settings.application.command.manageNightType.update;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateManageNightTypeRequest {
    private String name;
    private Status status;
    private String description;
}
