package com.kynsoft.finamer.invoicing.application.command.resourceType.update;

import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateManageResourceTypeRequest {

    private String description;
    private String name;
    private Status status;
    private Boolean defaults;
    private boolean invoice;
}
