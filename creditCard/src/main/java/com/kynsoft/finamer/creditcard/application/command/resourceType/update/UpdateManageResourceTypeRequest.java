package com.kynsoft.finamer.creditcard.application.command.resourceType.update;

import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateManageResourceTypeRequest {

    private String name;
    private Status status;
    private boolean vcc;
    private boolean defaults;
    private String description;
}
