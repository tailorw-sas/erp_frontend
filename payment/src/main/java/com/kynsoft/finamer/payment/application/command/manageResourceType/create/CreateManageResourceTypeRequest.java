package com.kynsoft.finamer.payment.application.command.manageResourceType.create;

import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateManageResourceTypeRequest {

    private String code;
    private String description;
    private Status status;
}
