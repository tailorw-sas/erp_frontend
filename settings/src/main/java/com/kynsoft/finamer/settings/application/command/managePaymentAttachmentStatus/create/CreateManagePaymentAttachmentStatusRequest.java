package com.kynsoft.finamer.settings.application.command.managePaymentAttachmentStatus.create;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateManagePaymentAttachmentStatusRequest {
    private String code;
    private String name;
    private Status status;
    private String navigate;
    private String module;
    private Boolean show;
    private String permissionCode;
    private String description;
}
