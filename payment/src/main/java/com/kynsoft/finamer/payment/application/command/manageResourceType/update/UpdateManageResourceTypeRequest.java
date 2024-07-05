package com.kynsoft.finamer.payment.application.command.manageResourceType.update;

import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateManageResourceTypeRequest {

    private String description;
    private Status status;
}
