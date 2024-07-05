package com.kynsoft.finamer.settings.application.command.manageAgencyType.create;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateManageAgencyTypeRequest {

    private String code;
    private Status status;
    private String name;
    private String description;
}
