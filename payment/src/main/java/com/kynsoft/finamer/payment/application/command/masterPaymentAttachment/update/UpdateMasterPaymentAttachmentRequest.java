package com.kynsoft.finamer.payment.application.command.masterPaymentAttachment.update;

import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateMasterPaymentAttachmentRequest {

    private Status status;
    private UUID resourceType;
    private UUID attachmentType;
    private String fileName;
    private String path;
    private String remark;
}
