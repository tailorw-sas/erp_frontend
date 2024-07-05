package com.kynsoft.finamer.payment.domain.dto;

import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MasterPaymentAttachmentDto {

    private UUID id;
    private Status status;
    private PaymentDto resource;
    private ResourceTypeDto resourceType;
    private AttachmentTypeDto attachmentType;
    private String fileName;
    private String path;
    private String remark;
}
