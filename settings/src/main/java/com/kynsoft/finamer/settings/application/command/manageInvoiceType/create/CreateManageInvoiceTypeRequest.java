package com.kynsoft.finamer.settings.application.command.manageInvoiceType.create;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateManageInvoiceTypeRequest {

    private String code;
    private String description;
    private Status status;
    private String name;
    private Boolean enabledToPolicy;
}
