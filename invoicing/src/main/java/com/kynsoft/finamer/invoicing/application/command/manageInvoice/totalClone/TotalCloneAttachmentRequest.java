package com.kynsoft.finamer.invoicing.application.command.manageInvoice.totalClone;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TotalCloneAttachmentRequest {

    private String filename;
    private String file;
    private String remark;
    private UUID type;
    private UUID invoice;
    private UUID paymentResourceType;
    private UUID employeeId;
    private String employeeName;
}
