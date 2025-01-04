package com.kynsoft.finamer.payment.application.command.attachmentType.create;

import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAttachmentTypeRequest {

    private String code;
    private String name;
    private String description;
    private Status status;
    private Boolean defaults;
    private boolean antiToIncomeImport;
}
