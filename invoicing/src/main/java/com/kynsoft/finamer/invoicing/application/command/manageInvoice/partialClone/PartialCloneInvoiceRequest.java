package com.kynsoft.finamer.invoicing.application.command.manageInvoice.partialClone;

import com.kynsoft.finamer.invoicing.application.command.manageAttachment.create.CreateAttachmentRequest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PartialCloneInvoiceRequest {
    private UUID invoice;
    private List<PartialCloneInvoiceAdjustmentRelation> roomRateAdjustments;
    private List<CreateAttachmentRequest> attachments;
    private String employee;

}
