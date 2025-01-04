package com.kynsoft.finamer.payment.application.command.masterPaymentAttachment.create;

import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateMasterPaymentAttachmentRequest {

    private Status status;
    private UUID employee;
    private UUID resource;
    private UUID resourceType;
    private UUID attachmentType;
    private String fileName;
    private String fileWeight;
    private String path;
    private String remark;
}
