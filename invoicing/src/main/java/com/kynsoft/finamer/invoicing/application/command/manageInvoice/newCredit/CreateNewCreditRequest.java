package com.kynsoft.finamer.invoicing.application.command.manageInvoice.newCredit;

import com.kynsoft.finamer.invoicing.application.command.manageAttachment.create.CreateAttachmentRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateNewCreditRequest {

    private UUID invoice;
    private LocalDateTime invoiceDate;
    private String employee;
    private List<CreateNewCreditBookingRequest> bookings;
    private List<CreateAttachmentRequest> attachments;
}
