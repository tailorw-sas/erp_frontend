package com.kynsoft.finamer.settings.application.command.manageAccountType.update;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateManagerAccountTypeRequest {
    private String name;
    private String description;
    private Status status;
}
