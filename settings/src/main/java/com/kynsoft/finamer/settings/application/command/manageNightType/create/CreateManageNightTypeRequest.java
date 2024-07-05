package com.kynsoft.finamer.settings.application.command.manageNightType.create;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateManageNightTypeRequest {
    private String code;
    private String name;
    private Status status;
    private String description;
}
