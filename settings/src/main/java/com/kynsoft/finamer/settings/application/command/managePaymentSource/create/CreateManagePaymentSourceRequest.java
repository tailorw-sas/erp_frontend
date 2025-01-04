package com.kynsoft.finamer.settings.application.command.managePaymentSource.create;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateManagePaymentSourceRequest {

    private String code;
    private String description;
    private Status status;
    private String name;
    private Boolean isBank;
    private Boolean expense;
}
