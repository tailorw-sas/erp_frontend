package com.kynsoft.finamer.creditcard.application.command.attachment.update;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAttachmentRequest {

    private UUID id;
    private String filename;
    private String file;
    private String remark;
    private UUID type;
    private UUID paymentResourceType;
    private String employee;
}
