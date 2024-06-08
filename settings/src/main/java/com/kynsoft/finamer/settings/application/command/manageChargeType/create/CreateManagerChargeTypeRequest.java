package com.kynsoft.finamer.settings.application.command.manageChargeType.create;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateManagerChargeTypeRequest {
    private String code;
    private String name;
    private String description;
    private Status status;
}
