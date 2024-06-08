package com.kynsoft.finamer.settings.application.command.manageB2BPartnerType.create;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateManageB2BPartnerTypeRequest {
    private String code;
    private String name;
    private String description;
    private Status status;
}
