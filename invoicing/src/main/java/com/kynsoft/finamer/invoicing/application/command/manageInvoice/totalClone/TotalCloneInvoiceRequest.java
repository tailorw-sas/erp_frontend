package com.kynsoft.finamer.invoicing.application.command.manageInvoice.totalClone;

import com.kynsoft.finamer.invoicing.application.command.manageAttachment.create.CreateAttachmentRequest;
import com.kynsoft.finamer.invoicing.application.command.manageBooking.create.CreateBookingRequest;
import com.kynsoft.finamer.invoicing.application.command.manageInvoice.create.CreateInvoiceRequest;

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
public class TotalCloneInvoiceRequest {
    private CreateInvoiceRequest invoice;
    private List<CreateBookingRequest> bookings;
    
    private List<CreateAttachmentRequest> attachments;
    private String employee;
    private UUID invoiceToClone;

}
