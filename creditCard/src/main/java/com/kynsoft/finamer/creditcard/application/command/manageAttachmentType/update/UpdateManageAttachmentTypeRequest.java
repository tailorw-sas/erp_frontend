package com.kynsoft.finamer.creditcard.application.command.manageAttachmentType.update;

import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateManageAttachmentTypeRequest {

    private String description;
    private Status status;
    private String name;
    private Boolean defaults;
}
