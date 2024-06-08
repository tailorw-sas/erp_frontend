package com.kynsoft.finamer.settings.application.command.manageAccountType.create;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateManagerAccountTypeRequest {
    private String code;
    private String name;
    private String description;
    private Status status;
}
