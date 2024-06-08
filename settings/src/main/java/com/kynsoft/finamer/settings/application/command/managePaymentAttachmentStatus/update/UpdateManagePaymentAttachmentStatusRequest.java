package com.kynsoft.finamer.settings.application.command.managePaymentAttachmentStatus.update;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateManagePaymentAttachmentStatusRequest {
    private String code;
    private String name;
    private Status status;
    private String navigate;
    private String module;
    private Boolean show;
    private String permissionCode;
    private String description;
}
