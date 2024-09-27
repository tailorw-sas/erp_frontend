package com.kynsoft.finamer.payment.application.command.resourceType.update;

import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
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
    private boolean invoiceDefault;
}
