package com.kynsoft.finamer.creditcard.application.command.resourceType.create;

import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateManageResourceTypeRequest {

    private String code;
    private String name;
    private String description;
    private Status status;
    private boolean vcc;
    private boolean defaults;
}
