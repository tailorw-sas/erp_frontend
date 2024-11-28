package com.kynsoft.finamer.creditcard.application.command.attachment.create;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateAttachmentRequest {

    private String filename;
    private String file;
    private String remark;
    private UUID type;
    private Long transaction;
    private String employee;
    private UUID employeeId;
    private UUID paymentResourceType;
    private UUID hotelPayment;
}
