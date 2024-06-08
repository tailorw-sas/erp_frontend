package com.kynsoft.finamer.settings.application.command.manageVCCTransactionType.update;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateManageVCCTransactionTypeRequest {

    private Status status;
    private String name;
    private String description;
    private Boolean isActive;

}
