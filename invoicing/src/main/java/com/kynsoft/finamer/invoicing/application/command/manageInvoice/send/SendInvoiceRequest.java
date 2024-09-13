package com.kynsoft.finamer.invoicing.application.command.manageInvoice.send;

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
public class SendInvoiceRequest {
    private List<UUID> invoice;
    private String employee;

}
