package com.kynsoft.finamer.creditcard.application.command.manageAttachmentType.create;

import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateManageAttachmentTypeRequest {

    private String code;
    private String description;
    private Status status;
    private String name;
    private boolean defaults;
}
