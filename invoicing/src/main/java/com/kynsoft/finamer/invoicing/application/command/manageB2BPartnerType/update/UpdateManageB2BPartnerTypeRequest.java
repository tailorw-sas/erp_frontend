package com.kynsoft.finamer.invoicing.application.command.manageB2BPartnerType.update;

import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateManageB2BPartnerTypeRequest {
    private String name;
    private String description;
    private Status status;
}
